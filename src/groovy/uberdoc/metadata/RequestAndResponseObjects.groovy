package uberdoc.metadata

import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import uberdoc.annotation.*
import uberdoc.messages.MessageReader

import java.lang.reflect.Field

/**
 * This class holds information about classes used as request and response objects along the API.
 * @see uberdoc.UberDocService
 */
@Log4j
class RequestAndResponseObjects {

    private Set requestAndResponseClasses = []
    private GrailsApplication grailsApplication
    private MessageReader messageReader

    RequestAndResponseObjects(GrailsApplication g, def messageSource, Locale locale) {
        grailsApplication = g
        messageReader = new MessageReader(messageSource, locale)
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
     * Extracts information from our annotations on each class and spits it out as a map
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
    private Map convertToMap(Set<Class> set) {
        Map<Class, Object> result = [:]

        set.each { Class clazz ->
            if (!clazz.isAnnotationPresent(UberDocModel)) {
                return
            }

            log.info("reading from $clazz")

            // collect class information
            String customDescription = messageReader.get("uberDoc.object.${clazz.simpleName}.description")

            Map clazzInfo = [:]
            clazzInfo << [name: clazz.simpleName]
            clazzInfo << [description: customDescription]
            clazzInfo << [properties: []]

            GrailsDomainClass domainClass = grailsApplication.getDomainClass(clazz.name) as GrailsDomainClass
            def domainClassConstraints = domainClass?.getConstrainedProperties()

            clazz.getProperties().declaredFields.findAll { field ->
                if (field.isAnnotationPresent(UberDocProperty)) {
                    Map fieldInformation = getProperties(field, domainClassConstraints, clazz.simpleName)
                    clazzInfo.properties << fieldInformation
                }
            }

            clazzInfo.properties.addAll(getImplicitProperties(clazz))

            result << [(clazz.simpleName): clazzInfo]
        }

        return result
    }

    private Map getProperties(Field field, def classConstraints, String objectName) {
        UberDocProperty propertyAnnotation = field.getAnnotation(UberDocProperty)

        // grab info from annotation
        Map propertyMap = [:]
        def constraints = []
        String customDescription = messageReader.get("uberDoc.object.${objectName}.${field.name}.description")
        String customSampleValue = messageReader.get("uberDoc.object.${objectName}.${field.name}.sampleValue")


        propertyMap << [name: field.name]
        propertyMap << [description: customDescription]
        propertyMap << [sampleValue: customSampleValue]
        propertyMap << [required: propertyAnnotation.required()]

        // get the type, also for Sets
        log.info("working on $objectName and ${field.name()}")
        if (field.type.name.endsWith("Set") || field.type.name.endsWith("List")) {
            if (field.signature) {
                propertyMap << [type: field.signature.split("/").last().split(";").first()]
            } else {
                log.error("No type defined for collection uberDoc.object.${objectName}.${field.name}")
                propertyMap << [type: "UNDEFINED"]
            }


            propertyMap << [isCollection: true]
        } else {
            propertyMap << [type: field.type.simpleName]
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

        propertyMap << [constraints: constraints]

        // check, if we should expose the id in case of a domain class


        return propertyMap
    }

    private List getImplicitProperties(Class clazz) {
        def result = []
        String customDescription = null
        String customSampleValue = null

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
                    required   : implicitProperty.required()
            ]
        }

        def explicitNames = clazz.getProperties().declaredFields.name

        if (implicitProperties) {
            implicitProperties.value().each { impl ->

                if (explicitNames.contains(impl.name())) {
                    log.error("Skipping creation of explicit parameter uberDoc.object.${clazz.simpleName}.${impl.name()}, as the actual property exists as well.")
                }

                customDescription = messageReader.get("uberDoc.object.${clazz.simpleName}.${impl.name()}.description")
                customSampleValue = messageReader.get("uberDoc.object.${clazz.simpleName}.${impl.name()}.sampleValue")

                result << [
                        name       : impl.name(),
                        type       : impl.type().simpleName,
                        description: customDescription,
                        sampleValue: customSampleValue,
                        required   : impl.required()
                ]
            }
        }

        return result
    }

}
