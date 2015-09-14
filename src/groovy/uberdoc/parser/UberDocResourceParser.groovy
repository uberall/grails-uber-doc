package uberdoc.parser

import org.codehaus.groovy.grails.commons.GrailsClass
import uberdoc.metadata.ControllerReader
import uberdoc.metadata.MethodReader

/**
 * This class is responsible for extracting information from {@class UberDocResource} annotation, make proper use of i18n
 * features and deliver a Map containing all of the information provided by that annotation.
 */
class UberDocResourceParser {
    MethodReader methodReader
    List<Map> genericErrors
    List<Map> genericHeaders
    ControllerReader controllerReader
    def messageSource

    UberDocResourceParser(GrailsClass controller, def ms) {
        messageSource = ms
        controllerReader = new ControllerReader(controller, messageSource)
        genericHeaders = controllerReader.headers
    }

    boolean isControllerSupported() {
        this.controllerReader.isControllerSupported()
    }

    Map parse(def controllerMethod, def mapping){
        Map restfulResource = [:]
        methodReader = new MethodReader(controllerMethod, messageSource, mapping.uri, mapping.method)
                .useGenericErrors(genericErrors)
                .useGenericHeaders(genericHeaders)

        restfulResource.title = methodReader.title
        restfulResource.description = methodReader.description
        restfulResource.uri = methodReader.uri
        restfulResource.method = mapping.method
        restfulResource.requestObject = methodReader.requestObject
        restfulResource.requestCollection = methodReader.requestIsCollection()
        restfulResource.responseObject = methodReader.responseObject
        restfulResource.responseCollection = methodReader.responseIsCollection()
        restfulResource.uriParams = methodReader.uriParams
        restfulResource.queryParams = methodReader.queryParams
        restfulResource.bodyParams = methodReader.bodyParams
        restfulResource.headers = methodReader.headers
        restfulResource.errors = methodReader.errors

        return restfulResource
    }

}
