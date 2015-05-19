package uberdoc

import uberdoc.annotation.UberDocResource
import uberdoc.metadata.ControllerReader
import uberdoc.metadata.GrailsReader
import uberdoc.metadata.MetadataReader
import uberdoc.metadata.MethodReader
import uberdoc.metadata.RequestAndResponseObjects
import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.web.mapping.UrlMappings

/**
 * This service is responsible for parsing @UberDoc annotations available in source code and structure it as a map,
 * returned to the invoking component for proper display and rendering.
 *
 * @see RequestAndResponseObjects
 */
class UberDocService {

    def grailsApplication
    UrlMappings grailsUrlMappingsHolder

    List<Map> genericErrors
    List<Map> genericHeaders
    List controllerMethods
    List controllerMappings
    Map restfulResource

    MethodReader methodReader
    ControllerReader controllerReader
    MetadataReader metadataReader
    GrailsReader grailsReader

    RequestAndResponseObjects objects

    /**
     * This method returns a Map with two root objects/information:
     *
     * 1- resources: contain structured information about all resources, using UrlMappings information as base. The basic idea is to:
     * * go through every controller;
     * * for each controller:
     * ** retrieve url mappings associated with that controller (e.g.: "/api/phods"(controller: 'phod', action: [POST: "create"]))
     * ** retrieve all methods available in that controller (e.g.: create, get, list)
     * ** for each method combine:
     * (i) the API information available in the annotations applied to that annotation (specific headers, specific errors, request objects),
     * (ii) the annotation info available at controller level (generic errors and headers),
     * (iii) information available in url mappings (e.g.: method (POST, GET, PATCH)
     *
     * 2- objects: contains information about all objects used either as request or response objects by all controllers
     *
     */
    Map getApiDocs() {
        Map apiInfo = [:]

        objects = new RequestAndResponseObjects(grailsApplication)
        metadataReader = new MetadataReader()
        grailsReader = new GrailsReader(grailsApplication, grailsUrlMappingsHolder)

        apiInfo.resources = []
        apiInfo.objects = [:]

        for(GrailsClass controller: grailsReader.controllers){
            controllerReader = new ControllerReader(controller)

            if(controllerReader.controllerIsSupported){
                genericErrors = controllerReader.errors
                genericHeaders = controllerReader.headers

                controllerMethods = grailsReader.getMethodsFrom(controller)
                controllerMappings = grailsReader.extractUrlMappingsFor(controller)

                controllerMappings.each { mapping ->
                    def controllerMethod = controllerMethods.find { it.name == mapping.name }

                    methodReader = new MethodReader(controllerMethod)
                            .useGenericErrors(genericErrors)
                            .useGenericHeaders(genericHeaders)

                    objects.extractFromResource(metadataReader.getAnnotation(UberDocResource).inMethod(controllerMethod))

                    restfulResource = [:]
                    restfulResource.description = methodReader.description
                    restfulResource.uri = replaceUriParams(mapping.uri, methodReader.uriParams)
                    restfulResource.method = mapping.method
                    restfulResource.requestObject = methodReader.requestObject
                    restfulResource.responseObject = methodReader.responseObject
                    restfulResource.responseCollection = methodReader.responseCollection
                    restfulResource.uriParams = methodReader.uriParams
                    restfulResource.queryParams = methodReader.queryParams
                    restfulResource.headers = methodReader.headers
                    restfulResource.errors = methodReader.errors

                    apiInfo.resources << restfulResource
                }
            }
        }

        apiInfo.objects = objects.fetch()

        return apiInfo
    }

    private String replaceUriParams(String uri, List<Map> uriParams){
        uri = uri.replaceAll("\\(\\*\\)", "{id}")

        uriParams.each {
            uri = uri.replaceFirst("\\{id\\}", it.name)
        }

        return uri
    }

}
