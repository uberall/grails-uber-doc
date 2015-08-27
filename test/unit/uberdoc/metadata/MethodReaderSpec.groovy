package uberdoc.metadata

import org.springframework.context.MessageSource
import sample.PodController
import spock.lang.Specification

class MethodReaderSpec extends Specification {

    MessageSource messageSourceMock

    def setup (){
        messageSourceMock = Mock()
    }

    def "useGenericHeaders works properly"(){
        given:
        MethodReader reader = new MethodReader(null, null)

        when:
        def r = reader.useGenericHeaders([[name: "header"]])

        then:
        r == reader
        [[name: "header"]] == reader.genericHeaders
    }

    def "useGenericErrors works properly"(){
        given:
        MethodReader reader = new MethodReader(null, null)

        when:
        def r = reader.useGenericErrors([[name: "header"]])

        then:
        r == reader
        [[name: "header"]] == reader.genericErrors
    }

    def "useURI works properly"(){
        given:
        MethodReader reader = new MethodReader(null, null)

        when:
        def r = reader.useURI("/api/pods/*/create")

        then:
        r == reader
        "api.pods.create" == reader.uri
    }

    def "useLocale overrides default locale"(){
        given:
        MethodReader reader = new MethodReader(null, null)

        when:
        def r = reader.useLocale(Locale.CANADA_FRENCH)

        then:
        r == reader
        Locale.CANADA_FRENCH == r.locale
        Locale.default != r.locale
    }

    def "if no locale is set, default is used"(){
        when:
        MethodReader reader = new MethodReader(null, null)

        then:
        Locale.default == reader.locale
    }

    def "getDescription returns the methods description if it's set"(){
        given:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' }, messageSourceMock)

        when:
        String desc = reader.description

        then:
        "this resource allows all Pods to be retrieved from DB" == desc
        1 * messageSourceMock.getMessage("uberDoc.null.description", new Object[0], Locale.default) >> "custom"
    }

    def "getDescription uses custom description if the methods description is not set"(){
        given:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'create' }, messageSourceMock)

        when:
        String desc = reader.description

        then:
        "custom" == desc
        1 * messageSourceMock.getMessage("uberDoc.null.description", new Object[0], Locale.default) >> "custom"
    }

    def "getRequestObject uses object"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'create' }, messageSourceMock)

        then:
        "Pod" == reader.requestObject
    }

    def "getRequestObject uses requestObject"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' }, messageSourceMock)

        then:
        "Pod" == reader.requestObject
    }

    def "getRequestObject returns null if method has no request object"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'list' }, messageSourceMock)

        then:
        !reader.requestObject
    }

    def "getRequestObject returns null if method is not annotated with @UberDocResource"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' }, messageSourceMock)

        then:
        !reader.requestObject
    }

    def "getResponseObject uses object"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'create' }, messageSourceMock)

        then:
        "Pod" == reader.responseObject
    }

    def "getResponseObject uses responseObject"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'list' }, messageSourceMock)

        then:
        "Pod" == reader.responseObject
    }

    def "getResponseObject returns null if method has no response object"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' }, messageSourceMock)

        then:
        !reader.responseObject
    }

    def "getResponseObject returns null if method is not annotated with @UberDocResource"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' }, messageSourceMock)

        then:
        !reader.responseObject
    }

    def "getResponseCollection works properly"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' }, messageSourceMock)

        then:
        "Pod" == reader.responseCollection
    }

    def "getResponseCollection returns null if method has no responseCollection object"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'list' }, messageSourceMock)

        then:
        !reader.responseCollection
    }

    def "getResponseCollection returns null if method is not annotated with @UberDocResource"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' }, messageSourceMock)

        then:
        !reader.responseCollection
    }

    def "getErrors works properly"(){
        given:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' }, messageSourceMock)

        when:
        def err = reader.errors

        then:
        1 == err.size()
        "returned if the resource does not exist" == err[0].description
        "NF404" == err[0].errorCode
        404 == err[0].httpCode
        1 * messageSourceMock.getMessage("uberDoc.null.error.404.description", new Object[0], Locale.default) >> "custom"
    }

    def "getErrors uses custom description if method does not define it"(){
        given:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'create' }, messageSourceMock)

        when:
        def err = reader.errors

        then:
        1 == err.size()
        "custom" == err[0].description
        "NF404" == err[0].errorCode
        404 == err[0].httpCode
        1 * messageSourceMock.getMessage("uberDoc.null.error.404.description", new Object[0], Locale.default) >> "custom"
    }

    def "getErrors uses generic errors into consideration properly"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' }, messageSourceMock)
                .useGenericErrors([[name: "value"]])

        then:
        reader.errors
        2 == reader.errors.size()
        "returned if the resource does not exist" == reader.errors[0].description
        "NF404" == reader.errors[0].errorCode
        404 == reader.errors[0].httpCode
        "value" == reader.errors[1].name
    }

    def "getErrors returns nothing if method does not have the annotation"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' }, messageSourceMock)

        then:
        !reader.errors
    }

    def "getErrors uses generic errors even if method does not have the annotation"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' }, messageSourceMock)
                .useGenericErrors([[name: "value"]])

        then:
        reader.errors
        1 == reader.errors.size()
        "value" == reader.errors[0].name
    }

    def "getHeaders works properly"(){
        given:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'list' }, messageSourceMock)

        when:
        def headers = reader.headers

        then:
        1 == headers.size()
        "hdr" == headers[0].name
        "sample" == headers[0].sampleValue
    }

    def "getHeaders uses custom description and sampleValue when they're not set"(){
        given:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'update' }, messageSourceMock)

        when:
        def headers = reader.headers

        then:
        1 == headers.size()
        "hdr" == headers[0].name
        "custom value" == headers[0].sampleValue
        "custom desc" == headers[0].description
        1 * messageSourceMock.getMessage("uberDoc.null.header.hdr.description", new Object[0], Locale.default) >> "custom desc"
        1 * messageSourceMock.getMessage("uberDoc.null.header.hdr.sampleValue", new Object[0], Locale.default) >> "custom value"
    }

    def "getHeaders uses generic headers into consideration properly"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'list' }, messageSourceMock)
                .useGenericHeaders([[name: "value"]])

        then:
        reader.headers
        2 == reader.headers.size()
        "hdr" == reader.headers[0].name
        "sample" == reader.headers[0].sampleValue
        "value" == reader.headers[1].name
    }

    def "getHeaders returns nothing if method does not have the annotation"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' }, messageSourceMock)

        then:
        !reader.headers
    }

    def "getHeaders uses generic headers even if method does not have the annotation"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' }, messageSourceMock)
                .useGenericHeaders([[name: "value"]])

        then:
        reader.headers
        1 == reader.headers.size()
        "value" == reader.headers[0].name
    }

    def "getUriParams works properly"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' }, messageSourceMock)

        then:
        reader.uriParams
        1 == reader.uriParams.size()
        "id" == reader.uriParams[0].name
        "the id of the Pod to be retrieved from DB" == reader.uriParams[0].description
        "4" == reader.uriParams[0].sampleValue
    }

    def "getUriParams works properly with custom messages"(){
        given:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'update' }, messageSourceMock)

        when:
        def uri = reader.uriParams

        then:
        1 == uri.size()
        "id" == uri[0].name
        "custom desc" == uri[0].description
        "custom value" == uri[0].sampleValue
        1 * messageSourceMock.getMessage("uberDoc.null.header.id.description", new Object[0], Locale.default) >> "custom desc"
        1 * messageSourceMock.getMessage("uberDoc.null.header.id.sampleValue", new Object[0], Locale.default) >> "custom value"
    }

    def "getUriParams works properly with mixed use of annotations"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'create' }, messageSourceMock)

        then:
        reader.uriParams
        3 == reader.uriParams.size()
        ["firstId", "secondId", "thirdId"].containsAll(reader.uriParams.name)
        ["1", "2", "3"].containsAll(reader.uriParams.sampleValue)
    }

    def "getUriParams returns nothing if method does not have the annotation"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' }, messageSourceMock)

        then:
        !reader.uriParams
    }

    def "getQueryParams works properly"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'update' }, messageSourceMock)

        then:
        reader.queryParams
        1 == reader.queryParams.size()
        "id" == reader.queryParams[0].name
        "the id of the Pod to be retrieved from DB" == reader.queryParams[0].description
        "4" == reader.queryParams[0].sampleValue
    }

    def "getQueryParams works properly with custom messages"(){
        given:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' }, messageSourceMock)

        when:
        def qry = reader.queryParams

        then:
        1 == qry.size()
        "id" == qry[0].name
        "custom desc" == qry[0].description
        "custom value" == qry[0].sampleValue
        1 * messageSourceMock.getMessage("uberDoc.null.header.id.description", new Object[0], Locale.default) >> "custom desc"
        1 * messageSourceMock.getMessage("uberDoc.null.header.id.sampleValue", new Object[0], Locale.default) >> "custom value"
    }

    def "getQueryParams works properly with mixed use of annotations"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'list' }, messageSourceMock)

        then:
        reader.queryParams
        2 == reader.queryParams.size()
        ["page", "max"].containsAll(reader.queryParams.name)
        ["pagination", "max desc"].containsAll(reader.queryParams.description)
        ["1", "100"].containsAll(reader.queryParams.sampleValue)
        [true, false].containsAll(reader.queryParams.required)
        [true, false].containsAll(reader.queryParams.isCollection)
    }

    def "getQueryParams returns nothing if method does not have the annotation"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'create' }, messageSourceMock)

        then:
        !reader.queryParams
    }
}
