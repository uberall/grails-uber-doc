package uberdoc.metadata

import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.web.mapping.UrlMappings

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

    GrailsClass[] getControllers(){
        return grailsApplication.controllerClasses
    }

    List getMethodsFrom(GrailsClass controller){
        return controller.clazz.methods
    }

    List extractUrlMappingsFor(GrailsClass controller){
        def mappedActions = grailsUrlMappingsHolder.urlMappings.findAll {
            it.controllerName == GrailsNameUtils.getPropertyNameRepresentation(controller.name)
        }

        if(!mappedActions){
            return []
        }

        def resources = []

        mappedActions.each { action ->
            if(action.actionName in Map){
                for(Map.Entry entry: action.actionName.entrySet()){
                    if(!action.parameterValues.action?.deprecated){
                        resources << [name: entry.value, uri: action.toString(), method: entry.key]
                    }
                }
            }
        }

        return resources
    }
}
