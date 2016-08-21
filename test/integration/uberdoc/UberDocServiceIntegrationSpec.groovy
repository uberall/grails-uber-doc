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
        def m = service.
        // get apiDocs a second time, to get the cached version
        m = service.apiDocs
        def somethingElse = m.resources?.find {it.method == 'POST' && it.uri == "/api/something/else"}
        def podsIdGet = m.resources?.find {it.method == 'GET' && it.uri == '/api/pods/$id'}
        def podsPost = m.resources?.find {it.method == 'POST' && it.uri == '/api/pods'}
        def podsGet = m.resources?.find {it.method == 'GET' && it.uri == '/api/pods'}
        def podsIdDelete = m.resources?.find {it.method == 'DELETE' && it.uri == '/api/pods/$id'}
        def podsIdPut = m.resources?.find {it.method == 'PUT' && it.uri == '/api/pods/$id'}

        then:
        m

        m.resources
        6 == m.resources.size()

        somethingElse
        "POST" == somethingElse.method
        "/api/something/else" == somethingElse.uri
        "Pod" == somethingElse.requestObject
        "Pod" == somethingElse.responseObject
        1 == somethingElse.headers.size()
        0 == somethingElse.queryParams.size()
        3 == somethingElse.uriParams.size()

        podsIdGet
        "GET" == podsIdGet.method
        '/api/pods/$id' == podsIdGet.uri
        !podsIdGet.requestObject
        "Pod" == podsIdGet.responseObject
        0 == podsIdGet.headers.size()
        1 == podsIdGet.errors.size()
        0 == podsIdGet.queryParams.size()
        1 == podsIdGet.uriParams.size()

        podsPost
        "POST" == podsPost.method
        "/api/pods" == podsPost.uri
        "Pod" == podsPost.requestObject
        "Pod" == podsPost.responseObject
        1 == podsPost.headers.size()
        1 == podsPost.errors.size()
        0 == podsPost.queryParams.size()
        3 == podsPost.uriParams.size()

        podsGet
        "GET" == podsGet.method
        '/api/pods' == podsGet.uri
        !podsGet.requestObject
        "Pod" == podsGet.responseObject
        1 == podsGet.headers.size()
        0 == podsGet.errors.size()
        2 == podsGet.queryParams.size()
        0 == podsGet.uriParams.size()

        podsIdDelete
        "DELETE" == podsIdDelete.method
        '/api/pods/$id' == podsIdDelete.uri
        !podsIdDelete.requestObject
        !podsIdDelete.responseObject
        0 == podsIdDelete.headers.size()
        0 == podsIdDelete.errors.size()
        0 == podsIdDelete.queryParams.size()
        1 == podsIdDelete.uriParams.size()

        podsIdPut
        "PUT" == podsIdPut.method
        '/api/pods/$id' == podsIdPut.uri
        "Pod" == podsIdPut.requestObject
        "Pod" == podsIdPut.responseObject
        1 == podsIdPut.headers.size()
        0 == podsIdPut.errors.size()
        1 == podsIdPut.queryParams.size()
        1 == podsIdPut.uriParams.size()

        m.objects

        3 == m.objects.size()
        m.objects."Persona" // as Persona is not declared as UberDocProperty in the Pod model, and is not returned by any controller
        m.objects."Spaceship" // even if not directly declared in the POD, we will include the UberDocModel of SpaceShip because it is referenced as an UberDocProperty (explicit or implicit)
        3 == m.objects."Pod".size()
        "Pod" == m.objects."Pod".name
        "overriden description for model" == m.objects."Pod".description
        9 == m.objects."Pod".properties.size()

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

        6 == m.objects."Pod".properties[4].size()
        "dateCreated" == m.objects."Pod".properties[4].name
        "Date" == m.objects."Pod".properties[4].type
        "uberDoc.object.Pod.dateCreated.description" == m.objects."Pod".properties[4].description
        "uberDoc.object.Pod.dateCreated.sampleValue" == m.objects."Pod".properties[4].sampleValue
        !m.objects."Pod".properties[4].required

        6 == m.objects."Pod".properties[5].size()
        "inherited" == m.objects."Pod".properties[5].name
        "String" == m.objects."Pod".properties[5].type
        "uberDoc.object.Pod.inherited.description" == m.objects."Pod".properties[5].description
        "uberDoc.object.Pod.inherited.sampleValue" == m.objects."Pod".properties[5].sampleValue
        !m.objects."Pod".properties[5].required
        !m.objects."Pod".properties[5].isCollection

        6 == m.objects."Pod".properties[6].size()
        "id" == m.objects."Pod".properties[6].name
        "Long" == m.objects."Pod".properties[6].type
        "uberDoc.object.Pod.id.description" == m.objects."Pod".properties[6].description
        "uberDoc.object.Pod.id.sampleValue" == m.objects."Pod".properties[6].sampleValue
        !m.objects."Pod".properties[6].required
        !m.objects."Pod".properties[6].isCollection

        6 == m.objects."Pod".properties[7].size()
        "longCollection" == m.objects."Pod".properties[7].name
        "Long" == m.objects."Pod".properties[7].type
        "uberDoc.object.Pod.longCollection.description" == m.objects."Pod".properties[7].description
        "uberDoc.object.Pod.longCollection.sampleValue" == m.objects."Pod".properties[7].sampleValue
        !m.objects."Pod".properties[7].required
        m.objects."Pod".properties[7].isCollection

        6 == m.objects."Pod".properties[8].size()
        "spaceship" == m.objects."Pod".properties[8].name
        "Spaceship" == m.objects."Pod".properties[8].type
        "uberDoc.object.Pod.spaceship.description" == m.objects."Pod".properties[8].description
        "uberDoc.object.Pod.spaceship.sampleValue" == m.objects."Pod".properties[8].sampleValue
        !m.objects."Pod".properties[8].required
        !m.objects."Pod".properties[8].isCollection

        3 == m.objects."Spaceship".size()
        "Spaceship" == m.objects."Spaceship".name
        "overriden description for Spaceship" == m.objects."Spaceship".description
        1 == m.objects."Spaceship".properties.size()

        6 == m.objects."Spaceship".properties[0].size()
        "dateCreated" == m.objects."Spaceship".properties[0].name
        "Date" == m.objects."Spaceship".properties[0].type
        "uberDoc.object.Spaceship.dateCreated.description" == m.objects."Spaceship".properties[0].description
        "uberDoc.object.Spaceship.dateCreated.sampleValue" == m.objects."Spaceship".properties[0].sampleValue
        !m.objects."Spaceship".properties[0].required
    }
}
