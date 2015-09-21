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
        1 == m.resources[0].headers.size()
        0 == m.resources[0].queryParams.size()
        3 == m.resources[0].uriParams.size()

        "GET" == m.resources[1].method
        '/api/pods/$id' == m.resources[1].uri
        !m.resources[1].requestObject
        "Pod" == m.resources[1].responseObject
        0 == m.resources[1].headers.size()
        1 == m.resources[1].errors.size()
        0 == m.resources[1].queryParams.size()
        1 == m.resources[1].uriParams.size()

        "POST" == m.resources[2].method
        "/api/pods" == m.resources[2].uri
        "Pod" == m.resources[2].requestObject
        "Pod" == m.resources[2].responseObject
        1 == m.resources[2].headers.size()
        1 == m.resources[2].errors.size()
        0 == m.resources[2].queryParams.size()
        3 == m.resources[2].uriParams.size()
        
        "GET" == m.resources[3].method
        '/api/pods' == m.resources[3].uri
        !m.resources[3].requestObject
        "Pod" == m.resources[3].responseObject
        1 == m.resources[3].headers.size()
        0 == m.resources[3].errors.size()
        2 == m.resources[3].queryParams.size()
        0 == m.resources[3].uriParams.size()

        "DELETE" == m.resources[4].method
        '/api/pods/$id' == m.resources[4].uri
        !m.resources[4].requestObject
        !m.resources[4].responseObject
        0 == m.resources[4].headers.size()
        0 == m.resources[4].errors.size()
        0 == m.resources[4].queryParams.size()
        1 == m.resources[4].uriParams.size()

        "PUT" == m.resources[5].method
        '/api/pods/$id' == m.resources[5].uri
        "Pod" == m.resources[5].requestObject
        "Pod" == m.resources[5].responseObject
        1 == m.resources[5].headers.size()
        0 == m.resources[5].errors.size()
        1 == m.resources[5].queryParams.size()
        1 == m.resources[5].uriParams.size()

        m.objects

        1 == m.objects.size()
        3 == m.objects."Pod".size()
        "Pod" == m.objects."Pod".name
        "overriden description for model" == m.objects."Pod".description
        6 == m.objects."Pod".properties.size()

        6 == m.objects."Pod".properties[0].size()
        "shared" == m.objects."Pod".properties[0].name
        "String" == m.objects."Pod".properties[0].type
        "uberDoc.object.Pod.shared.description" == m.objects."Pod".properties[0].description
        "uberDoc.object.Pod.shared.sampleValue" == m.objects."Pod".properties[0].sampleValue
        !m.objects."Pod".properties[0].required
        1 == m.objects."Pod".properties[0].constraints.size()
        "nullable" == m.objects."Pod".properties[0].constraints.first().constraint
        false == m.objects."Pod".properties[0].constraints.first().value

        6 == m.objects."Pod".properties[1].size()
        "license" == m.objects."Pod".properties[1].name
        "String" == m.objects."Pod".properties[1].type
        "uberDoc.object.Pod.license.description" == m.objects."Pod".properties[1].description
        "uberDoc.object.Pod.license.sampleValue" == m.objects."Pod".properties[1].sampleValue
        m.objects."Pod".properties[1].required
        2 == m.objects."Pod".properties[1].constraints.size()
        "blank" == m.objects."Pod".properties[1].constraints.first().constraint
        true == m.objects."Pod".properties[1].constraints.first().value
        "nullable" == m.objects."Pod".properties[1].constraints.last().constraint
        false == m.objects."Pod".properties[1].constraints.last().value

        6 == m.objects."Pod".properties[2].size()
        "botName" == m.objects."Pod".properties[2].name
        "String" == m.objects."Pod".properties[2].type
        "botName has a description" == m.objects."Pod".properties[2].description
        "botName has a sample value" == m.objects."Pod".properties[2].sampleValue
        !m.objects."Pod".properties[2].required
        2 == m.objects."Pod".properties[2].constraints.size()
        "custom" == m.objects."Pod".properties[2].constraints.first().constraint
        "uberDoc.object.Pod.constraints.custom" == m.objects."Pod".properties[2].constraints.first().value
        "nullable" == m.objects."Pod".properties[2].constraints.last().constraint
        false == m.objects."Pod".properties[2].constraints.last().value

        5 == m.objects."Pod".properties[3].size()
        "dateCreated" == m.objects."Pod".properties[3].name
        "Date" == m.objects."Pod".properties[3].type
        "uberDoc.object.Pod.dateCreated.description" == m.objects."Pod".properties[3].description
        "uberDoc.object.Pod.dateCreated.sampleValue" == m.objects."Pod".properties[3].sampleValue
        !m.objects."Pod".properties[3].required

        5 == m.objects."Pod".properties[4].size()
        "inherited" == m.objects."Pod".properties[4].name
        "String" == m.objects."Pod".properties[4].type
        "uberDoc.object.Pod.inherited.description" == m.objects."Pod".properties[4].description
        "uberDoc.object.Pod.inherited.sampleValue" == m.objects."Pod".properties[4].sampleValue
        !m.objects."Pod".properties[4].required

        5 == m.objects."Pod".properties[5].size()
        "id" == m.objects."Pod".properties[5].name
        "Long" == m.objects."Pod".properties[5].type
        "uberDoc.object.Pod.id.description" == m.objects."Pod".properties[5].description
        "uberDoc.object.Pod.id.sampleValue" == m.objects."Pod".properties[5].sampleValue
        !m.objects."Pod".properties[5].required
    }
}
