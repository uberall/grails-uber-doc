package uberdoc.metadata

import org.springframework.context.MessageSource
import sample.PodController
import spock.lang.Specification

class MethodReaderSpec extends Specification {

    MessageSource messageSourceMock

    void setup() {
        messageSourceMock = Mock()
    }

    void "useURI works properly"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'get'
        }, messageSourceMock, '/api/pods/(*)', 'GET')

        then:
        '/api/pods/$id' == reader.uri
        'api.pods.$id.GET' == reader.uriMessageKey
    }

    void "useLocale overrides default locale"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'get'
        }, messageSourceMock, '/api/pods/(*)', 'GET')

        when:
        def r = reader.useLocale(Locale.CANADA_FRENCH)

        then:
        r == reader
        Locale.CANADA_FRENCH == r.locale
        Locale.default != r.locale
    }

    void "if no locale is set, default is used"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'get'
        }, messageSourceMock, '/api/pods/(*)', 'GET')

        then:
        Locale.default == reader.locale
    }

    void "getResourceDescription returns message for key"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'get'
        }, messageSourceMock, '/api/pods/(*)', 'GET')
        reader.uriMessageKey = 'api.pods.$id.GET'

        when:
        String desc = reader.resourceDescription

        then:
        'description set by message' == desc
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.$id.GET.description', new Object[0], Locale.default) >> "description set by message"
    }

    void "getResourceDescription returns custom description"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'list'
        }, messageSourceMock, '/api/pods/', 'GET')

        when:
        String desc = reader.resourceDescription

        then:
        'custom description for list resource' == desc
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.GET.description', new Object[0], Locale.default) >> 'uberDoc.resource.api.pods.GET.description'
    }

    void "getResourceTitle returns message for key"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'get'
        }, messageSourceMock, '/api/pods/(*)', 'GET')
        reader.uriMessageKey = 'api.pods.$id.GET'

        when:
        String desc = reader.resourceTitle

        then:
        'title set by message' == desc
        1 * messageSourceMock.getMessage('uberDoc.api.pods.$id.GET.title', new Object[0], Locale.default) >> "title set by message"
    }

    void "getResourceTitle returns custom title"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'list'
        }, messageSourceMock, '/api/pods/', 'GET')

        when:
        String desc = reader.resourceTitle

        then:
        'custom title for list resource' == desc
        1 * messageSourceMock.getMessage('uberDoc.api.pods.GET.title', new Object[0], Locale.default) >> 'uberDoc.api.pods.GET.title'
    }

    void "getRequestObject uses object"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'create'
        }, messageSourceMock, '/api/pods/', 'POST')

        then:
        "Pod" == reader.requestObject
    }

    void "getRequestObject uses requestObject"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'update'
        }, messageSourceMock, '/api/pods/(*)', 'GET')

        then:
        "Pod" == reader.requestObject
    }

    void "getRequestObject returns null if method has no request object"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'list'
        }, messageSourceMock, '/api/pods/', 'GET')

        then:
        !reader.requestObject
    }

    void "getRequestObject returns null if method is not annotated with @UberDocResource"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'delete'
        }, messageSourceMock, '/api/pods/(*)', 'DELETE')

        then:
        !reader.requestObject
    }

    void "getResponseObject uses object"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'create'
        }, messageSourceMock, '/api/pods/', 'POST')

        then:
        "Pod" == reader.responseObject
    }

    void "getResponseObject uses responseObject"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'list'
        }, messageSourceMock, '/api/pods/', 'GET')

        then:
        "Pod" == reader.responseObject
    }

    void "getResponseObject returns null if method has no response object"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'delete'
        }, messageSourceMock, '/api/pods/(*)', 'DELETE')

        then:
        !reader.responseObject
    }

    void "getResponseObject returns null if method is not annotated with @UberDocResource"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'foobar'
        }, messageSourceMock, '/api/pods/(*)', 'GET')

        then:
        !reader.responseObject
    }

    void "getErrors works properly"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'get'
        }, messageSourceMock, '/api/pods/(*)', 'GET')

        when:
        def err = reader.errors

        then:
        1 == err.size()
        "description in message" == err[0].description
        "NF404" == err[0].errorCode
        404 == err[0].httpCode
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.$id.GET.error.404.description', new Object[0], Locale.default) >> "description in message"
    }

    void "getErrors works properly with custom values"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'create'
        }, messageSourceMock, '/api/pods/', 'POST')

        when:
        def err = reader.errors

        then:
        1 == err.size()
        "my sample error" == err[0].description
        "NF404" == err[0].errorCode
        404 == err[0].httpCode
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.POST.error.404.description', new Object[0], Locale.default) >> 'uberDoc.resource.api.pods.POST.error.404.description'
    }

    void "getHeaders works properly"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'list'
        }, messageSourceMock, '/api/pods/', 'GET')

        when:
        def headers = reader.headers

        then:
        1 == headers.size()
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.GET.header.hdr.sampleValue', new Object[0], Locale.default) >> "sampleValue in message"
        "hdr" == headers[0].name
        "sampleValue in message" == headers[0].sampleValue
    }

    void "getHeaders works properly with custom value"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'create'
        }, messageSourceMock, '/api/pods/', 'POST')

        when:
        def headers = reader.headers

        then:
        1 == headers.size()
        "some header param" == headers[0].name
        "my header" == headers[0].description
        "my sample value" == headers[0].sampleValue
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.POST.header.some.header.param.description', new Object[0], Locale.default) >> "uberDoc.resource.api.pods.POST.header.some.header.param.description"
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.POST.header.some.header.param.sampleValue', new Object[0], Locale.default) >> "uberDoc.resource.api.pods.POST.header.some.header.param.sampleValue"
    }

    void "getHeaders returns nothing if method does not have the annotation"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'delete'
        }, messageSourceMock, '/api/pods/(*)', 'DELETE')

        then:
        !reader.headers
    }

    void "getUriParams works properly"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'get'
        }, messageSourceMock, '/api/pods/(*)', 'GET')

        when:
        def uriParams = reader.uriParams

        then:
        1 == uriParams.size()
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.$id.GET.uriParam.id.description', new Object[0], Locale.default) >> "description in message"
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.$id.GET.uriParam.id.sampleValue', new Object[0], Locale.default) >> "sampleValue in message"
        "id" == uriParams[0].name
        "description in message" == uriParams[0].description
        "sampleValue in message" == uriParams[0].sampleValue
    }

    void "getUriParams works properly with custom values"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'update'
        }, messageSourceMock, '/api/pods/(*)', 'PATCH')

        when:
        def uriParams = reader.uriParams

        then:
        1 == uriParams.size()

        "id" == uriParams[0].name
        "custom description for id" == uriParams[0].description
        "custom sample value for id" == uriParams[0].sampleValue

        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.$id.PATCH.uriParam.id.description', new Object[0], Locale.default) >> 'uberDoc.resource.api.pods.$id.PATCH.uriParam.id.description'
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.$id.PATCH.uriParam.id.sampleValue', new Object[0], Locale.default) >> 'uberDoc.resource.api.pods.$id.PATCH.uriParam.id.sampleValue'
    }

    void "getUriParams works properly with mixed use of annotations"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'create'
        }, messageSourceMock, "foobar", "baz")

        then:
        reader.uriParams
        3 == reader.uriParams.size()
        ["firstId", "secondId", "thirdId"].containsAll(reader.uriParams.name)
    }

    void "getUriParams returns nothing if method does not have the annotation"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'list'
        }, messageSourceMock, '/api/pods/', 'GET')

        then:
        !reader.uriParams
    }

    void "getQueryParams works properly"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'update'
        }, messageSourceMock, '/api/pods/(*)', 'PATCH')

        when:
        def qry = reader.queryParams

        then:
        1 == qry.size()
        "foobar" == qry[0].name
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.$id.PATCH.queryParam.foobar.description', new Object[0], Locale.default) >> "description in message"
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.$id.PATCH.queryParam.foobar.sampleValue', new Object[0], Locale.default) >> "sampleValue in message"
        "description in message" == qry[0].description
        "sampleValue in message" == qry[0].sampleValue
    }

    void "getQueryParams works properly with custom description and sample value"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'list'
        }, messageSourceMock, '/api/pods/', 'GET')

        when:
        def qry = reader.queryParams

        then:
        2 == qry.size()

        "max" == qry[0].name
        qry[0].required
        "description in message" == qry[0].description
        "sampleValue in message" == qry[0].sampleValue

        "page" == qry[1].name
        !qry[1].required
        "custom description" == qry[1].description
        "custom value" == qry[1].sampleValue

        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.GET.queryParam.max.description', new Object[0], Locale.default) >> "description in message"
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.GET.queryParam.max.sampleValue', new Object[0], Locale.default) >> "sampleValue in message"
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.GET.queryParam.page.description', new Object[0], Locale.default) >> "uberDoc.resource.api.pods.GET.queryParam.page.description"
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.GET.queryParam.page.sampleValue', new Object[0], Locale.default) >> "uberDoc.resource.api.pods.GET.queryParam.page.sampleValue"
    }

    void "getQueryParams works properly with mixed use of annotations"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'list'
        }, messageSourceMock, '/api/pods/', 'GET')

        then:
        reader.queryParams
        2 == reader.queryParams.size()
        ["page", "max"].containsAll(reader.queryParams.name)
        [true, false].containsAll(reader.queryParams.required)
    }

    void "getQueryParams returns nothing if method does not have the annotation"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'create'
        }, messageSourceMock, '/api/pods/', 'POST')

        then:
        !reader.queryParams
    }

    void "getBodyParams works properly"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'create'
        }, messageSourceMock, '/api/pods/', 'POST')

        when:
        def bodyParams = reader.bodyParams

        then:
        2 == bodyParams.size()

        "firstBody" == bodyParams[0].name
        "description in message" == bodyParams[0].description
        "sampleValue in message" == bodyParams[0].sampleValue

        "secondBody" == bodyParams[1].name
        "2nd body desc" == bodyParams[1].description
        "body" == bodyParams[1].sampleValue

        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.POST.bodyParam.firstBody.description', new Object[0], Locale.default) >> "description in message"
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.POST.bodyParam.firstBody.sampleValue', new Object[0], Locale.default) >> "sampleValue in message"
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.POST.bodyParam.secondBody.description', new Object[0], Locale.default) >> "uberDoc.resource.api.pods.POST.bodyParam.secondBody.description"
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.POST.bodyParam.secondBody.sampleValue', new Object[0], Locale.default) >> "uberDoc.resource.api.pods.POST.bodyParam.secondBody.sampleValue"
    }
}
