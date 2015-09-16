package uberdoc.metadata

import uberdoc.annotation.*
import uberdoc.messages.MessageReader

/**
 * Extracts meta-information and annotations info from methods using reflection.
 */
class MethodReader {

    def method
    def messageSource
    String uri
    String uriMessageKey
    MetadataReader reader
    Locale locale
    MessageReader messageReader
    String httpMethod

    MethodReader(m, ms, mappingUri, mappingMethod) {
        reader = new MetadataReader()
        method = m
        messageSource = ms

        locale = Locale.default
        messageReader = new MessageReader(messageSource, locale)

        formatUriStrings(mappingUri, mappingMethod)
    }

    MethodReader formatUriStrings(def u, def httpMethod) {


        if (u) {
            // add the URI parameters to the URI as well
            uri = u.toString()

            this.httpMethod = httpMethod

            // replace variables in the URI with their respective name
            replaceUriParams()

            // if there are still variables placeholders, mark them as undefined parameters
            uriMessageKey = uri.replace("/", ".").replaceAll("\\(\\*\\)", "UNDEFINED_PARAMETER")

            if (uriMessageKey.startsWith(".")) {
                uriMessageKey = uriMessageKey.substring(1, uriMessageKey.length())
            }

            if (uriMessageKey.endsWith(".")) {
                uriMessageKey = uriMessageKey.substring(0, uriMessageKey.length() - 1)
            }

            uriMessageKey = "$uriMessageKey.$httpMethod"
        }
        return this
    }

    MethodReader useLocale(def l) {
        if (l) {
            locale = l
            messageReader = new MessageReader(messageSource, locale)
        }
        return this
    }

    String getBaseMessageKey() {
        return "uberDoc.resource.${uriMessageKey}"
    }

    String getTitle() {
        def uberDocResource = reader.getAnnotation(UberDocResource).inMethod(method)
        def customTitle = messageReader.get("uberDoc.${uriMessageKey}.title")
        return customTitle ?: uberDocResource?.title()
    }

    String getDescription() {
        def uberDocResource = reader.getAnnotation(UberDocResource).inMethod(method)
        def customDescription = messageReader.get("uberDoc.resource.${uriMessageKey}.description")
        return customDescription ?: uberDocResource?.description()
    }

    String getRequestObject() {
        UberDocResource uberDocResource = reader.getAnnotation(UberDocResource).inMethod(method)
        def requestObject = (uberDocResource?.requestObject() in Closure) ? null : uberDocResource?.requestObject()
        def object = (uberDocResource?.object() in Closure) ? null : uberDocResource?.object()
        return requestObject?.simpleName ?: object?.simpleName
    }

    String getResponseObject() {
        UberDocResource uberDocResource = reader.getAnnotation(UberDocResource).inMethod(method)
        def responseObject = (uberDocResource?.responseObject() in Closure) ? null : uberDocResource?.responseObject()
        def object = (uberDocResource?.object() in Closure) ? null : uberDocResource?.object()
        return responseObject?.simpleName ?: object?.simpleName
    }

    boolean responseIsCollection() {
        UberDocResource uberDocResource = reader.getAnnotation(UberDocResource).inMethod(method)
        return (uberDocResource?.responseIsCollection())
    }

    boolean requestIsCollection() {
        UberDocResource uberDocResource = reader.getAnnotation(UberDocResource).inMethod(method)
        return (uberDocResource?.requestIsCollection())
    }

    List<Map> getErrors() {
        def ret = []
        def methodErrors = reader.getAnnotation(UberDocErrors).inMethod(method)
        def singleError = reader.getAnnotation(UberDocError).inMethod(method)

        if (singleError) {
            ret << parseError(singleError)
        }

        if (methodErrors) {
            methodErrors.value().each {
                ret << parseError(it)
            }
        }

        return ret
    }

    private Map parseError(def err) {
        if (!err) {
            return [:]
        }
        def description = messageReader.get("uberDoc.resource.${uriMessageKey}.error.${err.httpCode()}.description")
        return [errorCode: err.errorCode(), httpCode: err.httpCode(), description: description]
    }

    List<Map> getHeaders() {
        def ret = []
        def methodHeaders = reader.getAnnotation(UberDocHeaders).inMethod(method)
        def singleHeader = reader.getAnnotation(UberDocHeader).inMethod(method)

        if (singleHeader) {
            ret << parseHeader(singleHeader)
        }

        if (methodHeaders) {
            methodHeaders.value().each {
                ret << parseHeader(it)
            }
        }

        return ret
    }

    private Map parseHeader(def hdr) {
        if (!hdr) {
            return [:]
        }
        def customDescription = messageReader.get("uberDoc.resource.${uriMessageKey}.header.${hdr.name()}.description")
        def customSampleValue = messageReader.get("uberDoc.resource.${uriMessageKey}.header.${hdr.name()}.sampleValue")
        return [name: hdr.name(), description: customDescription ?: hdr.description(), required: hdr.required(), sampleValue: customSampleValue ?: hdr.sampleValue()]
    }

    List<Map> getUriParams() {
        def ret = []
        def methodUriParams = reader.getAnnotation(UberDocUriParams).inMethod(method)
        def singleUriParam = reader.getAnnotation(UberDocUriParam).inMethod(method)

        if (singleUriParam) {
            ret << parseUriParam(singleUriParam)
        }

        if (methodUriParams) {
            methodUriParams.value().each {
                ret << parseUriParam(it)
            }
        }

        return ret
    }

    private Map parseUriParam(def urip) {
        if (!urip) {
            return [:]
        }
        def customDescription = messageReader.get("uberDoc.resource.${uriMessageKey}.uriParam.${urip.name()}.description")
        def customSampleValue = messageReader.get("uberDoc.resource.${uriMessageKey}.uriParam.${urip.name()}.sampleValue")
        return [name: urip.name(), description: customDescription ?: urip.description(), sampleValue: customSampleValue ?: urip.sampleValue()]
    }

    List<Map> getQueryParams() {
        def ret = []
        def methodQueryParams = reader.getAnnotation(UberDocQueryParams).inMethod(method)
        def singleQueryParam = reader.getAnnotation(UberDocQueryParam).inMethod(method)

        if (singleQueryParam) {
            ret << parseQueryParam(singleQueryParam)
        }

        if (methodQueryParams) {
            methodQueryParams.value().each {
                ret << parseQueryParam(it)
            }
        }

        return ret
    }

    private Map parseQueryParam(def qp) {
        if (!qp) {
            return [:]
        }

        def customDescription = messageReader.get("uberDoc.resource.${uriMessageKey}.queryParam.${qp.name()}.description")
        def customSampleValue = messageReader.get("uberDoc.resource.${uriMessageKey}.queryParam.${qp.name()}.sampleValue")

        return [
                name       : qp.name(),
                description: customDescription ?: qp.description(),
                sampleValue: customSampleValue ?: qp.sampleValue(),
                required   : qp.required(),
        ]
    }

    List<Map> getBodyParams() {
        def ret = []
        def methodBodyParams = reader.getAnnotation(UberDocBodyParams).inMethod(method)
        def singleQueryParam = reader.getAnnotation(UberDocBodyParam).inMethod(method)

        if (singleQueryParam) {
            ret << parseBodyParam(singleQueryParam)
        }

        if (methodBodyParams) {
            methodBodyParams.value().each {
                ret << parseBodyParam(it)
            }
        }

        return ret
    }

    private Map parseBodyParam(def bp) {
        if (!bp) {
            return [:]
        }

        def customDescription = messageReader.get("uberDoc.resource.${uriMessageKey}.bodyParam.${bp.name()}.description")
        def customSampleValue = messageReader.get("uberDoc.resource.${uriMessageKey}.bodyParam.${bp.name()}.sampleValue")

        return [
                name       : bp.name(),
                description: customDescription ?: bp.description(),
                sampleValue: customSampleValue ?: bp.sampleValue(),
                type       : bp.type().simpleName,
                required   : bp.required(),
        ]
    }

    private String replaceUriParams() {
        List<String> uriParamNames = []
        if (reader.getAnnotation(UberDocUriParams).inMethod(method)) uriParamNames << reader.getAnnotation(UberDocUriParams).inMethod(method).value().collect {
            it.name()
        }
        if (reader.getAnnotation(UberDocUriParam).inMethod(method)) uriParamNames.add(reader.getAnnotation(UberDocUriParam).inMethod(method).name())

        if (uriParamNames.size() == 0) return uri

        uriParamNames.each {
            uri = uri.replaceFirst("\\(\\*\\)", "\\\$$it")
        }

        return uri
    }


}
