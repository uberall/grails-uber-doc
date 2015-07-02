package uberdoc.metadata

import uberdoc.annotation.UberDocImplicitProperties
import uberdoc.annotation.UberDocImplicitProperty
import uberdoc.annotation.UberDocModel
import uberdoc.annotation.UberDocProperty
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass

import java.lang.reflect.Field

/**
 * This class holds information about classes used as request and response objects along the API.
 * @see uberdoc.UberDocService
 */
@Log4j
class RequestAndResponseObjects {

    private Set requestAndResponseClasses = []
    private GrailsApplication grailsApplication

    RequestAndResponseObjects(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication
    }

    void extractFromResource(def uberDocResource){

        if(!uberDocResource){
            return
        }

        if(uberDocResource.requestObject() && !(uberDocResource.requestObject() in Closure)){
            requestAndResponseClasses << uberDocResource.requestObject()
        }

        if(uberDocResource.responseObject() && !(uberDocResource.responseObject() in Closure)){
            requestAndResponseClasses << uberDocResource.responseObject()
        }

        if(uberDocResource.responseCollectionOf() && !(uberDocResource.responseCollectionOf() in Closure)){
            requestAndResponseClasses << uberDocResource.responseCollectionOf()
        }

        if(uberDocResource.object() && !(uberDocResource.object() in Closure)){
            requestAndResponseClasses << uberDocResource.object()
        }
    }

    Map fetch(){
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
            UberDocModel modelAnnotation = clazz.getAnnotation(UberDocModel)

            Map clazzInfo = [:]
            clazzInfo << [name: clazz.simpleName]
            clazzInfo << [description: modelAnnotation.description()]
            clazzInfo << [properties: []]

            GrailsDomainClass domainClass = grailsApplication.getDomainClass(clazz.name) as GrailsDomainClass
            def domainClassConstraints = domainClass?.getConstrainedProperties()

            // go over each field that is annotated and grab information from it
            getAllFields(clazz).each { Field field ->
                if (field.isAnnotationPresent(UberDocProperty)) {
                    Map fieldInformation = getProperties(field, domainClassConstraints)
                    clazzInfo.properties << fieldInformation
                }
            }

            clazzInfo.properties.addAll(getImplicitProperties(clazz))

            result << [(clazz.simpleName): clazzInfo]
        }

        return result
    }

    private Map getProperties(Field field, def classConstraints) {
        UberDocProperty propertyAnnotation = field.getAnnotation(UberDocProperty)

        // grab info from annotation
        Map propertyMap = [:]
        def constraints = []

        propertyMap << [name: field.name]
        propertyMap << [type: field.type.simpleName]
        propertyMap << [description: propertyAnnotation.description()]
        propertyMap << [sampleValue: propertyAnnotation.sampleValue()]
        propertyMap << [required: propertyAnnotation.required()]

        // read constraints
        if(classConstraints){
            classConstraints.entrySet().findAll{it.key == field.name}.each { constrainedProperty ->
                constrainedProperty.value.appliedConstraints.each { hibernateConstraint ->
                    if(hibernateConstraint.name != "validator"){
                        constraints << [constraint: hibernateConstraint.name, value: hibernateConstraint.constraintParameter]
                    }
                    else{
                        constraints << [constraint: "custom", value: "see object documentation"]
                    }
                }
            }
        }

        propertyMap << [constraints: constraints]

        return propertyMap
    }

    private List getImplicitProperties(Class clazz){
        def result = []
        UberDocImplicitProperty implicitProperty = clazz.getAnnotation(UberDocImplicitProperty)
        UberDocImplicitProperties implicitProperties = clazz.getAnnotation(UberDocImplicitProperties)

        if(implicitProperty){
            result << [
                    name: implicitProperty.name(),
                    type: implicitProperty.type().simpleName,
                    description: implicitProperty.description(),
                    sampleValue: implicitProperty.sampleValue(),
                    required: implicitProperty.required()
            ]
        }

        if(implicitProperties){
            implicitProperties.value().each { impl ->
                result << [
                        name: impl.name(),
                        type: impl.type().simpleName,
                        description: impl.description(),
                        sampleValue: impl.sampleValue(),
                        required: impl.required()
                ]
            }
        }

        return result
    }

    // @see http://stackoverflow.com/questions/1042798/retrieving-the-inherited-attribute-names-values-using-java-reflection
    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }
}
