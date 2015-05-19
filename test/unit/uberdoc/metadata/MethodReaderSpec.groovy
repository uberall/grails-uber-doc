package uberdoc.metadata

import sample.PodController
import spock.lang.Specification

class MethodReaderSpec extends Specification {

    def setup (){

    }

    def "useGenericHeaders works properly"(){
        given:
        MethodReader reader = new MethodReader()

        when:
        def r = reader.useGenericHeaders([[name: "header"]])

        then:
        r == reader
        [[name: "header"]] == reader.genericHeaders
    }

    def "useGenericErrors works properly"(){
        given:
        MethodReader reader = new MethodReader()

        when:
        def r = reader.useGenericErrors([[name: "header"]])

        then:
        r == reader
        [[name: "header"]] == reader.genericErrors
    }

    def "getDescription returns the methods description if it's set"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' })

        then:
        "this resource allows all Pods to be retrieved from DB" == reader.description
    }

    def "getDescription returns null if the methods description is not set"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'create' })

        then:
        !reader.description
    }

    def "getRequestObject uses object"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'create' })

        then:
        "Pod" == reader.requestObject
    }

    def "getRequestObject uses requestObject"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' })

        then:
        "Pod" == reader.requestObject
    }

    def "getRequestObject returns null if method has no request object"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'list' })

        then:
        !reader.requestObject
    }

    def "getRequestObject returns null if method is not annotated with @UberDocResource"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' })

        then:
        !reader.requestObject
    }

    def "getResponseObject uses object"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'create' })

        then:
        "Pod" == reader.responseObject
    }

    def "getResponseObject uses responseObject"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'list' })

        then:
        "Pod" == reader.responseObject
    }

    def "getResponseObject returns null if method has no response object"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' })

        then:
        !reader.responseObject
    }

    def "getResponseObject returns null if method is not annotated with @UberDocResource"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' })

        then:
        !reader.responseObject
    }

    def "getResponseCollection works properly"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' })

        then:
        "Pod" == reader.responseCollection
    }

    def "getResponseCollection returns null if method has no responseCollection object"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'list' })

        then:
        !reader.responseCollection
    }

    def "getResponseCollection returns null if method is not annotated with @UberDocResource"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' })

        then:
        !reader.responseCollection
    }

    def "getErrors works properly"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' })

        then:
        reader.errors
        1 == reader.errors.size()
        "returned if the resource does not exist" == reader.errors[0].description
        "NF404" == reader.errors[0].errorCode
        404 == reader.errors[0].httpCode
    }

    def "getErrors uses generic errors into consideration properly"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' })
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
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' })

        then:
        !reader.errors
    }

    def "getErrors uses generic errors even if method does not have the annotation"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' })
                .useGenericErrors([[name: "value"]])

        then:
        reader.errors
        1 == reader.errors.size()
        "value" == reader.errors[0].name
    }

    def "getHeaders works properly"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'list' })

        then:
        reader.headers
        1 == reader.headers.size()
        "hdr" == reader.headers[0].name
        "sample" == reader.headers[0].sampleValue
    }

    def "getHeaders uses generic headers into consideration properly"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'list' })
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
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' })

        then:
        !reader.headers
    }

    def "getHeaders uses generic headers even if method does not have the annotation"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' })
                .useGenericHeaders([[name: "value"]])

        then:
        reader.headers
        1 == reader.headers.size()
        "value" == reader.headers[0].name
    }

    def "getUriParams works properly"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' })

        then:
        reader.uriParams
        1 == reader.uriParams.size()
        "id" == reader.uriParams[0].name
        "the id of the Pod to be retrieved from DB" == reader.uriParams[0].description
        "4" == reader.uriParams[0].sampleValue
    }

    def "getUriParams works properly with mixed use of annotations"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'create' })

        then:
        reader.uriParams
        3 == reader.uriParams.size()
        ["firstId", "secondId", "thirdId"].containsAll(reader.uriParams.name)
        ["1", "2", "3"].containsAll(reader.uriParams.sampleValue)
    }

    def "getUriParams returns nothing if method does not have the annotation"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'delete' })

        then:
        !reader.uriParams
    }

    def "getQueryParams works properly"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'update' })

        then:
        reader.queryParams
        1 == reader.queryParams.size()
        "id" == reader.queryParams[0].name
        "the id of the Pod to be retrieved from DB" == reader.queryParams[0].description
        "4" == reader.queryParams[0].sampleValue
    }

    def "getQueryParams works properly with mixed use of annotations"(){
        when:
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'list' })

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
        MethodReader reader = new MethodReader(PodController.methods.find { it.name == 'get' })

        then:
        !reader.queryParams
    }
}
