package uberdoc.parser

import uberdoc.metadata.MethodReader

/**
 * This class is responsible for extracting information from {@class UberDocResource} annotation, make proper use of i18n
 * features and deliver a Map containing all of the information provided by that annotation.
 */
class UberDocResourceParser {
    MethodReader methodReader
    List<Map> genericHeaders
    def messageSource

    UberDocResourceParser(def ms) {
        messageSource = ms
    }

    Map parse(def controllerMethod, def mapping) {
        Map restfulResource = [:]
        methodReader = new MethodReader(controllerMethod, messageSource, mapping.uri, mapping.method)

        restfulResource.baseMessageKey = methodReader.baseMessageKey
        restfulResource.title = methodReader.resourceTitle
        restfulResource.description = methodReader.resourceDescription
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
