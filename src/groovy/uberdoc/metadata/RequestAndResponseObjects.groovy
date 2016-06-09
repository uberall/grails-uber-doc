package uberdoc.metadata

import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import uberdoc.annotation.*
import uberdoc.messages.MessageFallback
import uberdoc.messages.MessageReader

import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

/**
 * Holds information about classes used as request and response objects along the API.
 * @see uberdoc.UberDocService
 */
@Log4j
class RequestAndResponseObjects {

    private Set requestAndResponseClasses = []
    private GrailsApplication grailsApplication
    private MessageReader messageReader
    private MessageFallback fallback

    RequestAndResponseObjects(GrailsApplication g, messageSource, Locale locale) {
        grailsApplication = g
        messageReader = new MessageReader(messageSource, locale)
        fallback = new MessageFallback(messageReader)
    }

    void extractObjectsInfoFromResource(UberDocResource uberDocResource) {

        if (!uberDocResource) {
            return
        }

        if (uberDocResource.requestObject() && !(uberDocResource.requestObject() in Closure)) {
            requestAndResponseClasses << uberDocResource.requestObject()
        }

        if (uberDocResource.responseObject() && !(uberDocResource.responseObject() in Closure)) {
            requestAndResponseClasses << uberDocResource.responseObject()
        }

        if (uberDocResource.object() && !(uberDocResource.object() in Closure)) {
            requestAndResponseClasses << uberDocResource.object()
        }
    }

    Map fetch() {
        return convertToMap(requestAndResponseClasses)
    }

    /**
     * Extracts information from our annotations on each class and spits it out as a map, recursively extracts information also from properties if they are annotated correctly
     *
     * Each request/response class:
     * - description
     * - properties
     * - name
     * - type (String, int, Boolean, etc)
     * - constraints (custom > country specifics)
     * - description
     * - sample value
     *
     * @param set the set of classes to grab information from
     * @return a map
     */
    private Map convertToMap(Collection<Class> set, Map<Class, Object> result = [:]) {

        set.each { Class clazz ->
            // creates the clazzInfo object only if the property is annotated correctly and if we did not already convert it.
            if (!clazz.isAnnotationPresent(UberDocModel) || result.containsKey(clazz.simpleName)) {
                return
            }

            log.debug("reading from $clazz")

            // collect class information
            UberDocModel modelAnnotation = clazz.getAnnotation(UberDocModel)

            Map clazzInfo = [
                name: clazz.simpleName,
                description: fallback.fallbackToMessageSourceIfAnnotationDoesNotOverride("uberDoc.object.${clazz.simpleName}.description", modelAnnotation.description()),
                properties: []]

            clazzInfo.properties = getDeclaredFields(clazz)

            result[clazz.simpleName] = clazzInfo
            convertToMap(propertiesToConvert(clazz), result)
        }

        return result
    }

    /**
     * Extracts the classes of the properties that need to be converted to a map
     */
    private static Set<Class> propertiesToConvert(Class clazz) {
        Set<Class> toConvert = []
        clazz.getDeclaredFields().findAll {it.isAnnotationPresent(UberDocProperty)}?.genericType?.each {
            if (it in ParameterizedType) {
                toConvert.addAll(getClassesFromParameterizedType(it))
            } else {
                toConvert << it
            }
        }

        if (clazz.getAnnotation(UberDocExplicitProperty)) {
            toConvert << clazz.getAnnotation(UberDocExplicitProperty)?.type()
        }

        if (clazz.getAnnotation(UberDocExplicitProperties)) {
            clazz.getAnnotation(UberDocExplicitProperties).value().each {
                toConvert << it.type()
            }
        }
        return toConvert
    }

    private static List<Class> getClassesFromParameterizedType(ParameterizedType type) {
        List<Class> classes = []
        type.actualTypeArguments.each {
            if (it in ParameterizedType) {
                classes.addAll(getClassesFromParameterizedType(it))
            } else {
                classes << it
            }
        }
        return classes
    }

    /**
     * Returns a list of annotated Field of the given class
     * It also Recursivly scans superclass for annotated fields
     * @param clazz
     * @return the complete list of annotated fields
     */
    private List getDeclaredFields(Class clazz, Class childClassToUseForMessageKeys = null){
        def properties = []

        if(clazz.superclass != Object) {
            def fromSuper = getDeclaredFields(clazz.superclass, clazz)
            if(fromSuper)
                properties.addAll(fromSuper)
        }

        GrailsDomainClass domainClass = grailsApplication.getDomainClass(clazz.name) as GrailsDomainClass
        def domainClassConstraints = domainClass?.getConstrainedProperties()

        clazz.getProperties().declaredFields.findAll { field ->
            if (field.isAnnotationPresent(UberDocProperty)) {
                Map fieldInformation = getProperties(field, domainClassConstraints, childClassToUseForMessageKeys ? childClassToUseForMessageKeys.simpleName : clazz.simpleName)
                properties << fieldInformation
            }
        }

        properties.addAll(getImplicitProperties(clazz))
        properties.grep()
    }

    private Map getProperties(Field field, classConstraints, String objectName) {
        UberDocProperty propertyAnnotation = field.getAnnotation(UberDocProperty)

        // grab info from annotation
        def constraints = []

        Map propertyMap = [
            name: field.name,
            description: fallback.fallbackToMessageSourceIfAnnotationDoesNotOverride("uberDoc.object.${objectName}.${field.name}.description", propertyAnnotation.description()),
            sampleValue: fallback.fallbackToMessageSourceIfAnnotationDoesNotOverride("uberDoc.object.${objectName}.${field.name}.sampleValue", propertyAnnotation.sampleValue()),
            required: propertyAnnotation.required()]

        // get the type, also for Sets
        log.debug("working on $objectName and ${field.name}")
        if (field.type.name.endsWith("Set") || field.type.name.endsWith("List")) {
            if (field.signature) {
                propertyMap.type = field.signature.split("/").last().split(";").first()
            } else {
                log.warn("No type defined for collection uberDoc.object.${objectName}.${field.name}")
                propertyMap.type = "UNDEFINED"
            }
            propertyMap.isCollection = true
        } else {
            propertyMap.type = field.type.simpleName
        }

        // read constraints
        if (classConstraints) {
            classConstraints.entrySet().findAll { it.key == field.name }.each { constrainedProperty ->
                constrainedProperty.value.appliedConstraints.each { hibernateConstraint ->
                    if (hibernateConstraint.name != "validator") {
                        constraints << [constraint: hibernateConstraint.name, value: hibernateConstraint.constraintParameter]
                    } else {
                        constraints << [constraint: "custom", value: "uberDoc.object.${objectName}.constraints.custom"]
                    }
                }
            }
        }

        propertyMap.constraints = constraints

        return propertyMap
    }

    private List getImplicitProperties(Class clazz) {
        def result = []
        String customDescription
        String customSampleValue

        UberDocExplicitProperty implicitProperty = clazz.getAnnotation(UberDocExplicitProperty)
        UberDocExplicitProperties implicitProperties = clazz.getAnnotation(UberDocExplicitProperties)

        if (implicitProperty) {
            customDescription = messageReader.get("uberDoc.object.${clazz.simpleName}.${implicitProperty.name()}.description")
            customSampleValue = messageReader.get("uberDoc.object.${clazz.simpleName}.${implicitProperty.name()}.sampleValue")

            result << [
                    name       : implicitProperty.name(),
                    type       : implicitProperty.type().simpleName,
                    description: customDescription,
                    sampleValue: customSampleValue,
                    required   : implicitProperty.required(),
                    isCollection: implicitProperty.collection
            ]
        }

        def explicitNames = clazz.getProperties().declaredFields.name

        if (implicitProperties) {
            implicitProperties.value().each { impl ->

                if (explicitNames.contains(impl.name())) {
                    log.info("Skipping creation of explicit parameter uberDoc.object.${clazz.simpleName}.${impl.name()}, as the actual property exists as well.")
                }

                customDescription = messageReader.get("uberDoc.object.${clazz.simpleName}.${impl.name()}.description")
                customSampleValue = messageReader.get("uberDoc.object.${clazz.simpleName}.${impl.name()}.sampleValue")

                result << [
                        name       : impl.name(),
                        type       : impl.type().simpleName,
                        description: customDescription,
                        sampleValue: customSampleValue,
                        required   : impl.required(),
                        isCollection: impl.collection
                ]

            }
        }

        return result
    }

}
