package uberdoc.parser

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

    UberDocResourceParser(ControllerReader cr, def ms) {
        messageSource = ms
        controllerReader = cr
        genericErrors = controllerReader.errors
        genericHeaders = controllerReader.headers
    }

    Map parse(def controllerMethod, def mapping){
        Map restfulResource = [:]
        methodReader = new MethodReader(controllerMethod, messageSource)
                .useGenericErrors(genericErrors)
                .useGenericHeaders(genericHeaders)
                .useURI(mapping.uri)

        restfulResource.title = methodReader.title
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

        return restfulResource
    }

    private String replaceUriParams(String uri, List<Map> uriParams){
        uri = uri.replaceAll("\\(\\*\\)", "{id}")

        uriParams.each {
            uri = uri.replaceFirst("\\{id\\}", it.name)
        }

        return uri
    }

}
