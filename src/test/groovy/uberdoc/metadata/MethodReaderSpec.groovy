package uberdoc.metadata

import org.springframework.context.MessageSource
import sample.PodController
import spock.lang.Specification

class MethodReaderSpec extends Specification {

    MessageSource messageSourceMock

    void setup() {
        messageSourceMock = Mock(MessageSource)
    }

    void "useURI works properly"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'get'
        }, messageSourceMock, '/api/pods/(*)', 'GET')

        then:
        reader.uri == '/api/pods/$id'
        reader.uriMessageKey == 'api.pods.$id.GET'
    }

    void "useLocale overrides default locale"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'get'
        }, messageSourceMock, '/api/pods/(*)', 'GET')

        when:
        MethodReader r = reader.useLocale(Locale.CANADA_FRENCH)

        then:
        r == reader
        r.locale == Locale.CANADA_FRENCH
        Locale.default != r.locale
    }

    void "if no locale is set, default is used"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'get'
        }, messageSourceMock, '/api/pods/(*)', 'GET')

        then:
        reader.locale == Locale.default
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
        desc == 'description set by message'

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
        desc == 'custom description for list resource'

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
        desc == 'title set by message'

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
        desc == 'custom title for list resource'

        1 * messageSourceMock.getMessage('uberDoc.api.pods.GET.title', new Object[0], Locale.default) >> 'uberDoc.api.pods.GET.title'
    }

    void "getRequestObject uses object"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'create'
        }, messageSourceMock, '/api/pods/', 'POST')

        then:
        reader.requestObject == "Pod"
    }

    void "getRequestObject uses requestObject"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'update'
        }, messageSourceMock, '/api/pods/(*)', 'GET')

        then:
        reader.requestObject == "Pod"
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
        reader.responseObject == "Pod"
    }

    void "getResponseObject uses responseObject"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'list'
        }, messageSourceMock, '/api/pods/', 'GET')

        then:
        reader.responseObject == "Pod"
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
        List<Map> err = reader.errors

        then:
        err.size() == 1
        err[0].description == "description in message"
        err[0].errorCode == "NF404"
        err[0].httpCode == 404

        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.$id.GET.error.404.description', new Object[0], Locale.default) >> "description in message"
    }

    void "getErrors works properly with custom values"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'create'
        }, messageSourceMock, '/api/pods/', 'POST')

        when:
        List<Map> err = reader.errors

        then:
        err.size() == 1
        err[0].description == "my sample error"
        err[0].errorCode == "NF404"
        err[0].httpCode == 404

        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.POST.error.404.description', new Object[0], Locale.default) >> 'uberDoc.resource.api.pods.POST.error.404.description'
    }

    void "getHeaders works properly"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'list'
        }, messageSourceMock, '/api/pods/', 'GET')

        when:
        List<Map> headers = reader.headers

        then:
        headers.size() == 1
        headers[0].name == "hdr"
        headers[0].sampleValue == "sampleValue in message"

        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.GET.header.hdr.sampleValue', new Object[0], Locale.default) >> "sampleValue in message"
    }

    void "getHeaders works properly with custom value"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'create'
        }, messageSourceMock, '/api/pods/', 'POST')

        when:
        List<Map> headers = reader.headers

        then:
        headers.size() == 1
        headers[0].name == "some header param"
        headers[0].description == "my header"
        headers[0].sampleValue == "my sample value"

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
        List<Map> uriParams = reader.uriParams

        then:
        uriParams.size() == 1
        uriParams[0].name == "id"
        uriParams[0].description == "description in message"
        uriParams[0].sampleValue == "sampleValue in message"

        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.$id.GET.uriParam.id.description', new Object[0], Locale.default) >> "description in message"
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.$id.GET.uriParam.id.sampleValue', new Object[0], Locale.default) >> "sampleValue in message"
    }

    void "getUriParams works properly with custom values"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'update'
        }, messageSourceMock, '/api/pods/(*)', 'PATCH')

        when:
        List<Map> uriParams = reader.uriParams

        then:
        uriParams.size() == 1

        uriParams[0].name == "id"
        uriParams[0].description == "custom description for id"
        uriParams[0].sampleValue == "custom sample value for id"

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
        reader.uriParams.size() == 3
        reader.uriParams.name.containsAll(["firstId", "secondId", "thirdId"])
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
        List<Map> qry = reader.queryParams

        then:
        qry.size() == 1
        qry[0].name == "foobar"
        qry[0].description == "description in message"
        qry[0].sampleValue == "sampleValue in message"

        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.$id.PATCH.queryParam.foobar.description', new Object[0], Locale.default) >> "description in message"
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.$id.PATCH.queryParam.foobar.sampleValue', new Object[0], Locale.default) >> "sampleValue in message"
    }

    void "getQueryParams works properly with custom description and sample value"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'list'
        }, messageSourceMock, '/api/pods/', 'GET')

        when:
        List<Map> qry = reader.queryParams

        then:
        qry.size() == 2

        qry[0].name == "max"
        qry[0].required
        qry[0].description == "description in message"
        qry[0].sampleValue == "sampleValue in message"

        qry[1].name == "page"
        !qry[1].required
        qry[1].description == "custom description"
        qry[1].sampleValue == "custom value"

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
        reader.queryParams.size() == 2
        reader.queryParams.name.containsAll(["page", "max"])
        reader.queryParams.required.containsAll([true, false])
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
        List<Map> bodyParams = reader.bodyParams

        then:
        bodyParams.size() == 2

        bodyParams[0].name == "firstBody"
        bodyParams[0].description == "description in message"
        bodyParams[0].sampleValue == "sampleValue in message"

        bodyParams[1].name == "secondBody"
        bodyParams[1].description == "2nd body desc"
        bodyParams[1].sampleValue == "body"

        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.POST.bodyParam.firstBody.description', new Object[0], Locale.default) >> "description in message"
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.POST.bodyParam.firstBody.sampleValue', new Object[0], Locale.default) >> "sampleValue in message"
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.POST.bodyParam.secondBody.description', new Object[0], Locale.default) >> "uberDoc.resource.api.pods.POST.bodyParam.secondBody.description"
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.POST.bodyParam.secondBody.sampleValue', new Object[0], Locale.default) >> "uberDoc.resource.api.pods.POST.bodyParam.secondBody.sampleValue"
    }
}
