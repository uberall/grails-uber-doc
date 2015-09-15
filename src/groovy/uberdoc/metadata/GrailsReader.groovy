package uberdoc.metadata

import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.web.mapping.UrlMappings
import uberdoc.annotation.UberDocController
import uberdoc.annotation.UberDocResource

import java.lang.annotation.Annotation
import java.lang.reflect.Method

/**
 * This class provides higher class methods to interact with Grails configurations and metadata.
 */
class GrailsReader {

    def grailsApplication
    UrlMappings grailsUrlMappingsHolder

    GrailsReader(grailsApplication, UrlMappings grailsUrlMappingsHolder) {
        this.grailsApplication = grailsApplication
        this.grailsUrlMappingsHolder = grailsUrlMappingsHolder
    }

    GrailsClass[] getControllers() {
        return grailsApplication.controllerClasses.findAll { it.clazz.annotations.find { Annotation a -> a.annotationType() == UberDocController} }
    }

    static List getMethodsFrom(GrailsClass controller) {
        return controller.clazz.methods.findAll { Method m -> m.annotations && m.annotations.find { Annotation a -> a.annotationType() == UberDocResource}}
    }

    List extractUrlMappingsFor(GrailsClass controller) {
        def mappedActions = grailsUrlMappingsHolder.urlMappings.findAll {
            it.controllerName == GrailsNameUtils.getPropertyNameRepresentation(controller.name)
        }

        if (!mappedActions) {
            return []
        }

        def resources = []

        // take the first valid mapping for each mapped action; this will correspond to the last found most specific one in the URL mappings
        mappedActions.each { action ->
            if (action.actionName in Map) {
                for (Map.Entry entry : action.actionName.entrySet()) {
                    if (!action.parameterValues.action?.deprecated && !(resources.find {
                        it.method == entry.key && it.name == entry.value
                    })) {
                        resources << [name: entry.value, uri: action.toString(), method: entry.key]
                    }
                }
            }
        }

        return resources
    }
}
