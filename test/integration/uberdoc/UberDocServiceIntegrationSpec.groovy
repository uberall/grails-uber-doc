package uberdoc

import grails.test.spock.IntegrationSpec

class UberDocServiceIntegrationSpec extends IntegrationSpec {

    def grailsApplication
    def grailsUrlMappingsHolder
    def messageSource

    void "apiDocs only retrieves information from controllers annotated with @UberDocController that are not deprecated on UrlMappings"() {
        given:
        UberDocService service = new UberDocService(
                grailsApplication: grailsApplication,
                grailsUrlMappingsHolder: grailsUrlMappingsHolder,
                messageSource: messageSource
        )

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
        4 == m.resources[1].errors.size()
        0 == m.resources[1].queryParams.size()
        3 == m.resources[1].uriParams.size()

        "PUT" == m.resources[2].method
        "/api/pods/id" == m.resources[2].uri
        "Pod" == m.resources[2].requestObject
        "Pod" == m.resources[2].responseObject
        !m.resources[2].responseCollection
        3 == m.resources[2].headers.size()
        3 == m.resources[2].errors.size()
        1 == m.resources[2].queryParams.size()
        1 == m.resources[2].uriParams.size()

        "PATCH" == m.resources[3].method
        "/api/pods/id" == m.resources[3].uri
        "Pod" == m.resources[3].requestObject
        "Pod" == m.resources[3].responseObject
        !m.resources[3].responseCollection
        3 == m.resources[3].headers.size()
        3 == m.resources[3].errors.size()
        1 == m.resources[3].queryParams.size()
        1 == m.resources[3].uriParams.size()

        "GET" == m.resources[4].method
        "/api/pods/id" == m.resources[4].uri
        "Pod" == m.resources[4].requestObject
        !m.resources[4].responseObject
        "Pod" == m.resources[4].responseCollection
        2 == m.resources[4].headers.size()
        4 == m.resources[4].errors.size()
        1 == m.resources[4].queryParams.size()
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
        6 == m.objects."Pod".properties.size()

        6 == m.objects."Pod".properties[0].size()
        "license" == m.objects."Pod".properties[0].name
        "String" == m.objects."Pod".properties[0].type
        "license is used for ..." == m.objects."Pod".properties[0].description
        "DBNG3r" == m.objects."Pod".properties[0].sampleValue
        m.objects."Pod".properties[0].required
        2 == m.objects."Pod".properties[0].constraints.size()
        "blank" == m.objects."Pod".properties[0].constraints.first().constraint
        true == m.objects."Pod".properties[0].constraints.first().value
        "nullable" == m.objects."Pod".properties[0].constraints.last().constraint
        false == m.objects."Pod".properties[0].constraints.last().value

        6 == m.objects."Pod".properties[1].size()
        "botName" == m.objects."Pod".properties[1].name
        "String" == m.objects."Pod".properties[1].type
        "botName is used for movies credits ..." == m.objects."Pod".properties[1].description
        "C3PO" == m.objects."Pod".properties[1].sampleValue
        !m.objects."Pod".properties[1].required
        2 == m.objects."Pod".properties[1].constraints.size()
        "custom" == m.objects."Pod".properties[1].constraints.first().constraint
        "see object documentation" == m.objects."Pod".properties[1].constraints.first().value
        "nullable" == m.objects."Pod".properties[1].constraints.last().constraint
        false == m.objects."Pod".properties[1].constraints.last().value

        6 == m.objects."Pod".properties[2].size()
        "shared" == m.objects."Pod".properties[2].name
        "String" == m.objects."Pod".properties[2].type
        "shared is used for ..." == m.objects."Pod".properties[2].description
        "sherd" == m.objects."Pod".properties[2].sampleValue
        !m.objects."Pod".properties[2].required
        1 == m.objects."Pod".properties[2].constraints.size()
        "nullable" == m.objects."Pod".properties[2].constraints.last().constraint
        false == m.objects."Pod".properties[2].constraints.last().value

        5 == m.objects."Pod".properties[3].size()
        "dateCreated" == m.objects."Pod".properties[3].name
        "Date" == m.objects."Pod".properties[3].type
        "When the Pod was created" == m.objects."Pod".properties[3].description
        !m.objects."Pod".properties[3].sampleValue
        !m.objects."Pod".properties[3].required

        5 == m.objects."Pod".properties[4].size()
        "inherited" == m.objects."Pod".properties[4].name
        "String" == m.objects."Pod".properties[4].type
        "Just an inherited property" == m.objects."Pod".properties[4].description
        !m.objects."Pod".properties[4].sampleValue
        !m.objects."Pod".properties[4].required

        5 == m.objects."Pod".properties[5].size()
        "id" == m.objects."Pod".properties[5].name
        "Long" == m.objects."Pod".properties[5].type
        "Identifies a Pod" == m.objects."Pod".properties[5].description
        !m.objects."Pod".properties[5].sampleValue
        !m.objects."Pod".properties[5].required
    }
}
