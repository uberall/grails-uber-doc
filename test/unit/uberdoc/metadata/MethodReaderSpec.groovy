package uberdoc.metadata

import org.springframework.context.MessageSource
import sample.PodController
import spock.lang.Specification

class MethodReaderSpec extends Specification {

    MessageSource messageSourceMock

    def setup() {
        messageSourceMock = Mock()
    }

    def "useURI works properly"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'get'
        }, messageSourceMock, '/api/pods/(*)', 'GET')

        then:
        '/api/pods/$id' == reader.uri
        'api.pods.$id.GET' == reader.uriMessageKey
    }

    def "useLocale overrides default locale"() {
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

    def "if no locale is set, default is used"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'get'
        }, messageSourceMock, '/api/pods/(*)', 'GET')

        then:
        Locale.default == reader.locale
    }

    def "getDescription returns gets message for key"() {
        given:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'get'
        }, messageSourceMock, '/api/pods/(*)', 'GET')
        reader.uriMessageKey = 'api.pods.$id.GET'

        when:
        String desc = reader.description

        then:
        'description set by message' == desc
        1 * messageSourceMock.getMessage('uberDoc.resource.api.pods.$id.GET.description', new Object[0], Locale.default) >> "description set by message"
    }

    def "getRequestObject uses object"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'create'
        }, messageSourceMock, '/api/pods/', 'POST')

        then:
        "Pod" == reader.requestObject
    }

    def "getRequestObject uses requestObject"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'update'
        }, messageSourceMock, '/api/pods/(*)', 'GET')

        then:
        "Pod" == reader.requestObject
    }

    def "getRequestObject returns null if method has no request object"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'list'
        }, messageSourceMock, '/api/pods/', 'GET')

        then:
        !reader.requestObject
    }

    def "getRequestObject returns null if method is not annotated with @UberDocResource"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'delete'
        }, messageSourceMock, '/api/pods/(*)', 'DELETE')

        then:
        !reader.requestObject
    }

    def "getResponseObject uses object"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'create'
        }, messageSourceMock, '/api/pods/', 'POST')

        then:
        "Pod" == reader.responseObject
    }

    def "getResponseObject uses responseObject"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'list'
        }, messageSourceMock, '/api/pods/', 'GET')

        then:
        "Pod" == reader.responseObject
    }

    def "getResponseObject returns null if method has no response object"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'delete'
        }, messageSourceMock, '/api/pods/(*)', 'DELETE')

        then:
        !reader.responseObject
    }

    def "getResponseObject returns null if method is not annotated with @UberDocResource"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'foobar'
        }, messageSourceMock, '/api/pods/(*)', 'GET')

        then:
        !reader.responseObject
    }

    def "getErrors works properly"() {
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

    def "getHeaders works properly"() {
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

    def "getHeaders returns nothing if method does not have the annotation"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'delete'
        }, messageSourceMock, '/api/pods/(*)', 'DELETE')

        then:
        !reader.headers
    }

    def "getUriParams works properly"() {
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

    def "getUriParams works properly with mixed use of annotations"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'create'
        }, messageSourceMock, "foobar", "baz")

        then:
        reader.uriParams
        3 == reader.uriParams.size()
        ["firstId", "secondId", "thirdId"].containsAll(reader.uriParams.name)
    }

    def "getUriParams returns nothing if method does not have the annotation"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'list'
        }, messageSourceMock, '/api/pods/', 'GET')

        then:
        !reader.uriParams
    }

    def "getQueryParams works properly"() {
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

    def "getQueryParams works properly with mixed use of annotations"() {
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

    def "getQueryParams returns nothing if method does not have the annotation"() {
        when:
        MethodReader reader = new MethodReader(PodController.methods.find {
            it.name == 'create'
        }, messageSourceMock, '/api/pods/', 'POST')

        then:
        !reader.queryParams
    }
}
