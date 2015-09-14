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
        7 == m.resources.size()

        "POST" == m.resources[0].method
        "/api/something/else" == m.resources[0].uri
        "Pod" == m.resources[0].requestObject
        "Pod" == m.resources[0].responseObject
        3 == m.resources[0].headers.size()
        0 == m.resources[0].queryParams.size()
        3 == m.resources[0].uriParams.size()

        "POST" == m.resources[1].method
        "/api/pods" == m.resources[1].uri
        "Pod" == m.resources[1].requestObject
        "Pod" == m.resources[1].responseObject
        3 == m.resources[1].headers.size()
        1 == m.resources[1].errors.size()
        0 == m.resources[1].queryParams.size()
        3 == m.resources[1].uriParams.size()

        "GET" == m.resources[2].method
        '/api/pods/$id' == m.resources[5].uri
        !m.resources[5].requestObject
        "Pod" == m.resources[2].responseObject
        true == m.resources[2].responseCollection
        3 == m.resources[2].headers.size()
        0 == m.resources[2].errors.size()
        2 == m.resources[2].queryParams.size()
        0 == m.resources[2].uriParams.size()

        "PUT" == m.resources[3].method
        '/api/pods/$id' == m.resources[3].uri
        "Pod" == m.resources[3].requestObject
        "Pod" == m.resources[3].responseObject
        3 == m.resources[3].headers.size()
        0 == m.resources[3].errors.size()
        1 == m.resources[3].queryParams.size()
        1 == m.resources[3].uriParams.size()

        "PATCH" == m.resources[4].method
        '/api/pods/$id' == m.resources[4].uri
        "Pod" == m.resources[4].requestObject
        "Pod" == m.resources[4].responseObject
        3 == m.resources[4].headers.size()
        0 == m.resources[4].errors.size()
        1 == m.resources[4].queryParams.size()
        1 == m.resources[4].uriParams.size()

        "GET" == m.resources[5].method
        '/api/pods/$id' == m.resources[5].uri
        !m.resources[5].requestObject
        "Pod" == m.resources[5].responseObject
        2 == m.resources[5].headers.size()
        1 == m.resources[5].errors.size()
        0 == m.resources[5].queryParams.size()
        1 == m.resources[5].uriParams.size()

        "DELETE" == m.resources[6].method
        '/api/pods/$id' == m.resources[6].uri
        !m.resources[6].requestObject
        !m.resources[6].responseObject
        2 == m.resources[6].headers.size()
        0 == m.resources[6].errors.size()
        0 == m.resources[6].queryParams.size()
        1 == m.resources[6].uriParams.size()

        m.objects

        1 == m.objects.size()
        3 == m.objects."Pod".size()
        "Pod" == m.objects."Pod".name
        "uberDoc.object.Pod.description" == m.objects."Pod".description
        5 == m.objects."Pod".properties.size()

        6 == m.objects."Pod".properties[0].size()
        "license" == m.objects."Pod".properties[0].name
        "String" == m.objects."Pod".properties[0].type
        "uberDoc.object.Pod.license.description" == m.objects."Pod".properties[0].description
        "uberDoc.object.Pod.license.sampleValue" == m.objects."Pod".properties[0].sampleValue
        m.objects."Pod".properties[0].required
        2 == m.objects."Pod".properties[0].constraints.size()
        "blank" == m.objects."Pod".properties[0].constraints.first().constraint
        true == m.objects."Pod".properties[0].constraints.first().value
        "nullable" == m.objects."Pod".properties[0].constraints.last().constraint
        false == m.objects."Pod".properties[0].constraints.last().value

        6 == m.objects."Pod".properties[1].size()
        "botName" == m.objects."Pod".properties[1].name
        "String" == m.objects."Pod".properties[1].type
        "uberDoc.object.Pod.botName.description" == m.objects."Pod".properties[1].description
        "uberDoc.object.Pod.botName.sampleValue" == m.objects."Pod".properties[1].sampleValue
        !m.objects."Pod".properties[1].required
        2 == m.objects."Pod".properties[1].constraints.size()
        "custom" == m.objects."Pod".properties[1].constraints.first().constraint
        "uberDoc.object.Pod.constraints.custom" == m.objects."Pod".properties[1].constraints.first().value
        "nullable" == m.objects."Pod".properties[1].constraints.last().constraint
        false == m.objects."Pod".properties[1].constraints.last().value

        5 == m.objects."Pod".properties[4].size()
        "id" == m.objects."Pod".properties[4].name
        "Long" == m.objects."Pod".properties[4].type
        "uberDoc.object.Pod.id.description" == m.objects."Pod".properties[4].description
        "uberDoc.object.Pod.id.sampleValue" == m.objects."Pod".properties[4].sampleValue
        !m.objects."Pod".properties[4].required
    }
}
