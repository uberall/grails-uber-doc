package uberdoc.metadata

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.codehaus.groovy.grails.commons.GrailsClass
import org.springframework.context.MessageSource
import sample.OtherController
import sample.PodController
import spock.lang.Specification
import uberdoc.annotation.*

@TestMixin(ControllerUnitTestMixin)
class ControllerReaderSpec extends Specification {

    MetadataReader metadataReaderMock
    UberDocErrors errorsMock
    UberDocHeaders headersMock
    UberDocController controllerMock
    UberDocError errorMock
    UberDocHeader headerMock
    MessageSource messageSourceMock

    def setup(){
        metadataReaderMock = Mock()
        errorsMock = Mock()
        controllerMock = Mock()
        headersMock = Mock()
        errorMock = Mock()
        headerMock = Mock()
        messageSourceMock = Mock()
    }

    void "getHeaders for PodController gets header annotations"() {
        given:
        ControllerReader reader = new ControllerReader(PodController.asType(GrailsClass), messageSourceMock)

        and:
        headerMock.name() >> "name"
        headerMock.required() >> true

        and:
        headersMock.value() >> headerMock

        and:
        metadataReaderMock.getAnnotation(_) >> metadataReaderMock
        metadataReaderMock.inController(_) >> headersMock

        reader.metadataReader = metadataReaderMock

        when:
        def m = reader.getHeaders()

        then:
        m
        1 == m.size()
        "name" == m[0].name
        m[0].required
    }

    void "getHeaders for PodController returns a map with correct messages"() {
        given:
        ControllerReader reader = new ControllerReader(PodController.asType(GrailsClass), messageSourceMock)

        and:
        headerMock.name() >> "name"
        headerMock.required() >> true

        and:
        headersMock.value() >> headerMock

        and:
        metadataReaderMock.getAnnotation(_) >> metadataReaderMock
        metadataReaderMock.inController(_) >> headersMock

        reader.metadataReader = metadataReaderMock

        when:
        def m = reader.getHeaders()

        then:
        m
        1 == m.size()
        "description in message" == m[0].description
        "sampleValue in message" == m[0].sampleValue
        "name" == m[0].name
        m[0].required
        1 * messageSourceMock.getMessage('uberDoc.object.Class1_groovyProxy.genericHeader.name.description', new Object[0], Locale.default) >> "description in message"
        1 * messageSourceMock.getMessage('uberDoc.object.Class1_groovyProxy.genericHeader.name.sampleValue', new Object[0], Locale.default) >> "sampleValue in message"
    }

    void "getHeaders for PodController returns an empty map if annotation has no values"() {
        given:
        ControllerReader reader = new ControllerReader(PodController.asType(GrailsClass), messageSourceMock)

        and:
        headersMock.value() >> []

        and:
        metadataReaderMock.getAnnotation(_) >> metadataReaderMock
        metadataReaderMock.inController(_) >> headersMock

        reader.metadataReader = metadataReaderMock

        when:
        def m = reader.getHeaders()

        then:
        !m
    }

    void "if reader returns nothing, getHeaders returns an empty map"() {
        given:
        ControllerReader reader = new ControllerReader(PodController.asType(GrailsClass), messageSourceMock)

        and:
        metadataReaderMock.getAnnotation(_) >> metadataReaderMock
        metadataReaderMock.inController(_) >> null

        reader.metadataReader = metadataReaderMock

        when:
        def m = reader.getHeaders()

        then:
        !m
    }

    void "isSupported returns true for PodController if metadata reader returns an instance"() {
        given:
        ControllerReader reader = new ControllerReader(PodController.asType(GrailsClass), messageSourceMock)

        and:
        headersMock.value() >> []

        and:
        metadataReaderMock.getAnnotation(_) >> metadataReaderMock
        metadataReaderMock.inController(_) >> controllerMock

        reader.metadataReader = metadataReaderMock

        when:
        def m = reader.controllerSupported

        then:
        m
    }

    void "isSupported returns false for OtherController if metadata reader returns null"() {
        given:
        ControllerReader reader = new ControllerReader(OtherController.asType(GrailsClass), messageSourceMock)

        and:
        headersMock.value() >> []

        and:
        metadataReaderMock.getAnnotation(_) >> metadataReaderMock
        metadataReaderMock.inController(_) >> null

        reader.metadataReader = metadataReaderMock

        when:
        def m = reader.controllerSupported

        then:
        !m
    }
}
