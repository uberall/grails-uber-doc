package uberdoc.parser

import uberdoc.metadata.MethodReader

/**
 * Extracts information from {@class UberDocResource} annotation, makes proper use of i18n
 * features and delivers a Map containing all of the information provided by that annotation.
 */
class UberDocResourceParser {

    MethodReader methodReader
    def messageSource

    UberDocResourceParser(ms) {
        messageSource = ms
    }

    Map parse(controllerMethod, mapping) {
        methodReader = new MethodReader(controllerMethod, messageSource, mapping.uri, mapping.method)

        [baseMessageKey    : methodReader.baseMessageKey,
         title             : methodReader.resourceTitle,
         description       : methodReader.resourceDescription,
         uri               : methodReader.uri,
         method            : mapping.method,
         requestObject     : methodReader.requestObject,
         requestCollection : methodReader.requestIsCollection(),
         responseObject    : methodReader.responseObject,
         responseCollection: methodReader.responseIsCollection(),
         uriParams         : methodReader.uriParams,
         queryParams       : methodReader.queryParams,
         bodyParams        : methodReader.bodyParams,
         headers           : methodReader.headers,
         errors            : methodReader.errors,
         examples          : methodReader.examples]
    }
}
