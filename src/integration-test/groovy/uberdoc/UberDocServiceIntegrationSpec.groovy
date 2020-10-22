package uberdoc

import grails.core.GrailsApplication
import grails.test.mixin.integration.Integration
import grails.web.mapping.UrlMappings
import org.springframework.context.MessageSource
import spock.lang.Specification

@Integration
class UberDocServiceIntegrationSpec extends Specification {

    GrailsApplication grailsApplication
    MessageSource messageSource
    UrlMappings grailsUrlMappingsHolder

    void "apiDocs retrieves sorted information from controllers annotated with @UberDocController"() {
        given:
        UberDocService service = new UberDocService(
                grailsApplication: grailsApplication,
                grailsUrlMappingsHolder: grailsUrlMappingsHolder,
                messageSource: messageSource
        )

        when:
        service.apiDocs
        // get apiDocs a second time, to get the cached version
        ApiDocumentation docs = service.apiDocs
        Map somethingElse = docs.resources?.find { it.method == 'POST' && it.uri == "/api/something/else" } as Map
        Map podsIdGet = docs.resources?.find { it.method == 'GET' && it.uri == '/api/pods/$id' } as Map
        Map podsPost = docs.resources?.find { it.method == 'POST' && it.uri == '/api/pods' } as Map
        Map podsGet = docs.resources?.find { it.method == 'GET' && it.uri == '/api/pods' } as Map
        Map podsIdDelete = docs.resources?.find { it.method == 'DELETE' && it.uri == '/api/pods/$id' } as Map
        Map podsIdPut = docs.resources?.find { it.method == 'PUT' && it.uri == '/api/pods/$id' } as Map

        then:
        docs

        docs.resources
        docs.resources.size() == 6

        somethingElse
        somethingElse.method == "POST"
        somethingElse.requestObject == "Pod"
        somethingElse.responseObject == "Pod"
        somethingElse.headers.size() == 1
        somethingElse.queryParams.size() == 0
        somethingElse.uriParams.size() == 3
        somethingElse.examples as String == [
                examples: [
                        [
                                name        : "Some example",
                                query_params: [
                                        lang   : "en",
                                        version: 20181010
                                ],
                                body        : [
                                        businessId: 123,
                                        locationId: 456],
                                response    : [
                                        statusCode: 200,
                                        output    : [
                                                message: "Example message"
                                        ],
                                ]
                        ]
                ]
        ] as String

        podsIdGet
        podsIdGet.method == "GET"
        !podsIdGet.requestObject
        podsIdGet.responseObject == "Pod"
        podsIdGet.headers.size() == 0
        podsIdGet.errors.size() == 1
        podsIdGet.queryParams.size() == 0
        podsIdGet.uriParams.size() == 1

        podsPost
        podsPost.method == "POST"
        podsPost.requestObject == "Pod"
        podsPost.responseObject == "Pod"
        podsPost.headers.size() == 1
        podsPost.errors.size() == 1
        podsPost.queryParams.size() == 0
        podsPost.uriParams.size() == 3
        podsPost.uriParams.name == ['firstId', 'secondId', 'thirdId']

        podsGet
        podsGet.method == "GET"
        !podsGet.requestObject
        podsGet.responseObject == "Pod"
        podsGet.headers.size() == 1
        podsGet.errors.size() == 0
        podsGet.queryParams.size() == 2
        podsGet.queryParams.name == ['max', 'page']
        podsGet.uriParams.size() == 0

        podsIdDelete
        podsIdDelete.method == "DELETE"
        !podsIdDelete.requestObject
        !podsIdDelete.responseObject
        podsIdDelete.headers.size() == 0
        podsIdDelete.errors.size() == 0
        podsIdDelete.queryParams.size() == 0
        podsIdDelete.uriParams.size() == 1

        podsIdPut
        podsIdPut.method == "PUT"
        podsIdPut.requestObject == "Pod"
        podsIdPut.responseObject == "Pod"
        podsIdPut.headers.size() == 1
        podsIdPut.errors.size() == 0
        podsIdPut.queryParams.size() == 1
        podsIdPut.uriParams.size() == 1

        docs.objects

        docs.objects.size() == 3
        docs.objects."Persona" // as Persona is not declared as UberDocProperty in the Pod model, and is not returned by any controller
        docs.objects."Spaceship" // even if not directly declared in the POD, we will include the UberDocModel of SpaceShip because it is referenced as an UberDocProperty (explicit or implicit)
        docs.objects."Pod".size() == 3
        docs.objects."Pod".name == "Pod"
        docs.objects."Pod".description == "overriden description for model"
        docs.objects."Pod".properties.size() == 9

        docs.objects."Pod".properties[0].size() == 6
        docs.objects."Pod".properties[0].name == "botName"
        docs.objects."Pod".properties[0].type == "String"
        docs.objects."Pod".properties[0].description == "botName has a description"
        docs.objects."Pod".properties[0].sampleValue == "botName has a sample value"
        !docs.objects."Pod".properties[0].required
        docs.objects."Pod".properties[0].constraints.size() == 2
        docs.objects."Pod".properties[0].constraints.first().constraint == "custom"
        docs.objects."Pod".properties[0].constraints.first().value == "uberDoc.object.Pod.constraints.custom"
        docs.objects."Pod".properties[0].constraints.last().constraint == "nullable"
        docs.objects."Pod".properties[0].constraints.last().value == false

        docs.objects."Pod".properties[1].size() == 6
        docs.objects."Pod".properties[1].name == "dateCreated"
        docs.objects."Pod".properties[1].type == "Date"
        docs.objects."Pod".properties[1].description == "uberDoc.object.Pod.dateCreated.description"
        docs.objects."Pod".properties[1].sampleValue == "uberDoc.object.Pod.dateCreated.sampleValue"
        !docs.objects."Pod".properties[1].required

        docs.objects."Pod".properties[2].size() == 6
        docs.objects."Pod".properties[2].name == "id"
        docs.objects."Pod".properties[2].type == "Long"
        docs.objects."Pod".properties[2].description == "uberDoc.object.Pod.id.description"
        docs.objects."Pod".properties[2].sampleValue == "uberDoc.object.Pod.id.sampleValue"
        !docs.objects."Pod".properties[2].required
        !docs.objects."Pod".properties[2].isCollection

        docs.objects."Pod".properties[3].size() == 6
        docs.objects."Pod".properties[3].name == "inherited"
        docs.objects."Pod".properties[3].type == "String"
        docs.objects."Pod".properties[3].description == "uberDoc.object.Pod.inherited.description"
        docs.objects."Pod".properties[3].sampleValue == "uberDoc.object.Pod.inherited.sampleValue"
        !docs.objects."Pod".properties[3].required
        !docs.objects."Pod".properties[3].isCollection

        docs.objects."Pod".properties[4].size() == 6
        docs.objects."Pod".properties[4].name == "license"
        docs.objects."Pod".properties[4].type == "String"
        docs.objects."Pod".properties[4].description == "uberDoc.object.Pod.license.description"
        docs.objects."Pod".properties[4].sampleValue == "uberDoc.object.Pod.license.sampleValue"
        docs.objects."Pod".properties[4].required
        docs.objects."Pod".properties[4].constraints.size() == 2
        docs.objects."Pod".properties[4].constraints.first().constraint == "blank"
        docs.objects."Pod".properties[4].constraints.first().value == true
        docs.objects."Pod".properties[4].constraints.last().constraint == "nullable"
        docs.objects."Pod".properties[4].constraints.last().value == false

        docs.objects."Pod".properties[5].size() == 6
        docs.objects."Pod".properties[5].name == "longCollection"
        docs.objects."Pod".properties[5].type == "Long"
        docs.objects."Pod".properties[5].description == "uberDoc.object.Pod.longCollection.description"
        docs.objects."Pod".properties[5].sampleValue == "uberDoc.object.Pod.longCollection.sampleValue"
        !docs.objects."Pod".properties[5].required
        docs.objects."Pod".properties[5].isCollection

        docs.objects."Pod".properties[6].size() == 6
        docs.objects."Pod".properties[6].name == "persons"
        docs.objects."Pod".properties[6].type == "Map"
        docs.objects."Pod".properties[6].description == "uberDoc.object.Pod.persons.description"
        docs.objects."Pod".properties[6].sampleValue == "uberDoc.object.Pod.persons.sampleValue"
        !docs.objects."Pod".properties[6].required
        !docs.objects."Pod".properties[6].isCollection

        docs.objects."Pod".properties[7].size() == 6
        docs.objects."Pod".properties[7].name == "shared"
        docs.objects."Pod".properties[7].type == "String"
        docs.objects."Pod".properties[7].description == "uberDoc.object.Pod.shared.description"
        docs.objects."Pod".properties[7].sampleValue == "uberDoc.object.Pod.shared.sampleValue"
        !docs.objects."Pod".properties[7].required
        docs.objects."Pod".properties[7].constraints.size() == 1
        docs.objects."Pod".properties[7].constraints.first().constraint == "nullable"
        docs.objects."Pod".properties[7].constraints.first().value == false

        docs.objects."Pod".properties[8].size() == 6
        docs.objects."Pod".properties[8].name == "spaceship"
        docs.objects."Pod".properties[8].type == "Spaceship"
        docs.objects."Pod".properties[8].description == "uberDoc.object.Pod.spaceship.description"
        docs.objects."Pod".properties[8].sampleValue == "uberDoc.object.Pod.spaceship.sampleValue"
        !docs.objects."Pod".properties[8].required
        !docs.objects."Pod".properties[8].isCollection

        docs.objects."Spaceship".size() == 3
        docs.objects."Spaceship".name == "Spaceship"
        docs.objects."Spaceship".description == "overriden description for Spaceship"
        docs.objects."Spaceship".properties.size() == 1

        docs.objects."Spaceship".properties[0].size() == 6
        docs.objects."Spaceship".properties[0].name == "dateCreated"
        docs.objects."Spaceship".properties[0].type == "Date"
        docs.objects."Spaceship".properties[0].description == "uberDoc.object.Spaceship.dateCreated.description"
        docs.objects."Spaceship".properties[0].sampleValue == "uberDoc.object.Spaceship.dateCreated.sampleValue"
        !docs.objects."Spaceship".properties[0].required
    }

    void "internalOnly controllers and their methods only get published if uberdoc.publishInternalOnly is true"() {
        given:
        grailsApplication.config.uberdoc.setAt('publishInternalOnly', publish)

        UberDocService service = new UberDocService(
                grailsApplication: grailsApplication,
                grailsUrlMappingsHolder: grailsUrlMappingsHolder,
                messageSource: messageSource
        )

        when:
        ApiDocumentation docs = service.apiDocs

        then:
        docs.resources.any { it.method == 'GET' && it.uri == '/api/internal/$id' } == publish
        docs.resources.any { it.method == 'GET' && it.uri == '/api/internal' } == publish

        where:
        publish << [true, false]
    }

    void "internalOnly methods only get published if uberdoc.publishInternalOnly is true"() {
        given:
        grailsApplication.config.uberdoc.setAt('publishInternalOnly', publish)

        UberDocService service = new UberDocService(
                grailsApplication: grailsApplication,
                grailsUrlMappingsHolder: grailsUrlMappingsHolder,
                messageSource: messageSource
        )

        when:
        ApiDocumentation docs = service.apiDocs

        then:
        docs.resources?.any { it.method == 'GET' && it.uri == '/api/pods/internal' } == publish

        where:
        publish << [true, false]
    }

    void "internalOnly models only get published if uberdoc.publishInternalOnly is true"() {
        given:
        grailsApplication.config.uberdoc.setAt('publishInternalOnly', publish)

        UberDocService service = new UberDocService(
                grailsApplication: grailsApplication,
                grailsUrlMappingsHolder: grailsUrlMappingsHolder,
                messageSource: messageSource
        )

        when:
        ApiDocumentation docs = service.apiDocs

        then:
        docs.objects.any { it.key == "Internal" } == publish

        where:
        publish << [true, false]
    }

    void "internalOnly properties only get published if uberdoc.publishInternalOnly is true"() {
        given:
        grailsApplication.config.uberdoc.setAt('publishInternalOnly', publish)

        UberDocService service = new UberDocService(
                grailsApplication: grailsApplication,
                grailsUrlMappingsHolder: grailsUrlMappingsHolder,
                messageSource: messageSource
        )

        when:
        ApiDocumentation docs = service.apiDocs

        then:
        docs.objects.Persona.properties.any { it.name == "internalField" } == publish

        where:
        publish << [true, false]
    }
}
