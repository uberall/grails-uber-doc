package uberdoc

import grails.test.spock.IntegrationSpec

class UberDocServiceIntegrationSpec extends IntegrationSpec {

    def grailsApplication
    def grailsUrlMappingsHolder

    void "apiDocs only retrieves information from controllers annotated with @UberDocController that are not deprecated on UrlMappings"() {
        given:
        UberDocService service = new UberDocService(grailsApplication: grailsApplication, grailsUrlMappingsHolder: grailsUrlMappingsHolder)

        when:
        def m = service.apiDocs

        then:
        m

        m.resources
        6 == m.resources.size()

        "POST" == m.resources[0].method
        "/api/something/else" == m.resources[0].uri
        "Pod" == m.resources[0].requestObject
        "Pod" == m.resources[0].responseObject
        !m.resources[0].responseCollection
        3 == m.resources[0].headers.size()
        3 == m.resources[0].errors.size()
        0 == m.resources[0].queryParams.size()
        3 == m.resources[0].uriParams.size()

        "POST" == m.resources[1].method
        "/api/pods" == m.resources[1].uri
        "Pod" == m.resources[1].requestObject
        "Pod" == m.resources[1].responseObject
        !m.resources[1].responseCollection
        3 == m.resources[1].headers.size()
        3 == m.resources[1].errors.size()
        0 == m.resources[1].queryParams.size()
        3 == m.resources[1].uriParams.size()

        "PUT" == m.resources[2].method
        "/api/pods/{id}" == m.resources[2].uri
        "Pod" == m.resources[2].requestObject
        "Pod" == m.resources[2].responseObject
        !m.resources[2].responseCollection
        2 == m.resources[2].headers.size()
        3 == m.resources[2].errors.size()
        1 == m.resources[2].queryParams.size()
        0 == m.resources[2].uriParams.size()

        "PATCH" == m.resources[3].method
        "/api/pods/{id}" == m.resources[3].uri
        "Pod" == m.resources[3].requestObject
        "Pod" == m.resources[3].responseObject
        !m.resources[3].responseCollection
        2 == m.resources[3].headers.size()
        3 == m.resources[3].errors.size()
        1 == m.resources[3].queryParams.size()
        0 == m.resources[3].uriParams.size()

        "GET" == m.resources[4].method
        "/api/pods/id" == m.resources[4].uri
        "Pod" == m.resources[4].requestObject
        !m.resources[4].responseObject
        "Pod" == m.resources[4].responseCollection
        2 == m.resources[4].headers.size()
        4 == m.resources[4].errors.size()
        0 == m.resources[4].queryParams.size()
        1 == m.resources[4].uriParams.size()

        "DELETE" == m.resources[5].method
        "/api/pods/{id}" == m.resources[5].uri
        !m.resources[5].requestObject
        !m.resources[5].responseObject
        !m.resources[5].responseCollection
        2 == m.resources[5].headers.size()
        3 == m.resources[5].errors.size()
        1 == m.resources[5].queryParams.size()
        0 == m.resources[5].uriParams.size()

        m.objects

        1 == m.objects.size()
        3 == m.objects."Pod".size()
        "Pod" == m.objects."Pod".name
        "This class does something..." == m.objects."Pod".description
        2 == m.objects."Pod".properties.size()

        5 == m.objects."Pod".properties.first().size()
        "license" == m.objects."Pod".properties.first().name
        "String" == m.objects."Pod".properties.first().type
        "license is used for ..." == m.objects."Pod".properties.first().description
        "DBNG3r" == m.objects."Pod".properties.first().sampleValue
        2 == m.objects."Pod".properties.first().constraints.size()
        "blank" == m.objects."Pod".properties.first().constraints.first().constraint
        true == m.objects."Pod".properties.first().constraints.first().value
        "nullable" == m.objects."Pod".properties.first().constraints.last().constraint
        false == m.objects."Pod".properties.first().constraints.last().value

        5 == m.objects."Pod".properties.last().size()
        "botName" == m.objects."Pod".properties.last().name
        "String" == m.objects."Pod".properties.last().type
        "botName is used for movies credits ..." == m.objects."Pod".properties.last().description
        "C3PO" == m.objects."Pod".properties.last().sampleValue
        2 == m.objects."Pod".properties.last().constraints.size()
        "custom" == m.objects."Pod".properties.last().constraints.first().constraint
        "see object documentation" == m.objects."Pod".properties.last().constraints.first().value
        "nullable" == m.objects."Pod".properties.first().constraints.last().constraint
        false == m.objects."Pod".properties.first().constraints.last().value
    }
}
