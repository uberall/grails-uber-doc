package uberdoc.parser

import org.springframework.context.MessageSource
import uberdoc.metadata.MethodReader

/**
 * Extracts information from {@class UberDocResource} annotation, makes proper use of i18n
 * features and delivers a Map containing all of the information provided by that annotation.
 */
class UberDocResourceParser {

    Locale locale
    MethodReader methodReader
    MessageSource messageSource

    UberDocResourceParser(MessageSource ms, Locale locale = Locale.default) {
        messageSource = ms
        this.locale = locale
    }

    Map parse(controllerMethod, mapping) {
        methodReader = new MethodReader(controllerMethod, messageSource, mapping.uri, mapping.method, locale)

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
         examples          : methodReader.examples,
         internalOnly      : methodReader.isInternalOnly()]
    }
}
