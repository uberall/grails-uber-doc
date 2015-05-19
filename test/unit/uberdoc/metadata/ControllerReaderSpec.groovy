package uberdoc.metadata

import uberdoc.annotation.UberDocController
import uberdoc.annotation.UberDocError
import uberdoc.annotation.UberDocErrors
import uberdoc.annotation.UberDocHeader
import uberdoc.annotation.UberDocHeaders
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.codehaus.groovy.grails.commons.GrailsClass
import sample.OtherController
import sample.PodController
import spock.lang.Specification

@TestMixin(ControllerUnitTestMixin)
class ControllerReaderSpec extends Specification {

    MetadataReader metadataReaderMock
    UberDocErrors errorsMock
    UberDocHeaders headersMock
    UberDocController controllerMock
    UberDocError errorMock
    UberDocHeader headerMock

    def setup(){
        metadataReaderMock = Mock()
        errorsMock = Mock()
        controllerMock = Mock()
        headersMock = Mock()
        errorMock = Mock()
        headerMock = Mock()
    }

    void "getErrors for PodController returns a map with 3 elements"() {
        given:
        ControllerReader reader = new ControllerReader(PodController.asType(GrailsClass))

        and:
        errorMock.description() >> "desc"
        errorMock.errorCode() >> "errCode"
        errorMock.httpCode() >> 101

        and:
        errorsMock.value() >> errorMock

        and:
        metadataReaderMock.getAnnotation(_) >> metadataReaderMock
        metadataReaderMock.inController(_) >> errorsMock

        reader.metadataReader = metadataReaderMock

        when:
        def m = reader.getErrors()

        then:
        m
        1 == m.size()
        "desc" == m[0].description
        "errCode" == m[0].errorCode
        101 == m[0].httpCode
    }

    void "getErrors for PodController returns a map with null values if annotation is not complete"() {
        given:
        ControllerReader reader = new ControllerReader(PodController.asType(GrailsClass))

        and:
        errorMock.description() >> "desc"
        errorMock.errorCode() >> "errCode"

        and:
        errorsMock.value() >> errorMock

        and:
        metadataReaderMock.getAnnotation(_) >> metadataReaderMock
        metadataReaderMock.inController(_) >> errorsMock

        reader.metadataReader = metadataReaderMock

        when:
        def m = reader.getErrors()

        then:
        m
        1 == m.size()
        "desc" == m[0].description
        "errCode" == m[0].errorCode
        !m[0].httpCode
    }

    void "getErrors for PodController returns an empty map if annotation has no values"() {
        given:
        ControllerReader reader = new ControllerReader(PodController.asType(GrailsClass))

        and:
        errorsMock.value() >> []

        and:
        metadataReaderMock.getAnnotation(_) >> metadataReaderMock
        metadataReaderMock.inController(_) >> errorsMock

        reader.metadataReader = metadataReaderMock

        when:
        def m = reader.getErrors()

        then:
        !m
    }

    void "if reader returns nothing, getErrors returns an empty map"() {
        given:
        ControllerReader reader = new ControllerReader(PodController.asType(GrailsClass))

        and:
        metadataReaderMock.getAnnotation(_) >> metadataReaderMock
        metadataReaderMock.inController(_) >> null

        reader.metadataReader = metadataReaderMock

        when:
        def m = reader.getErrors()

        then:
        !m
    }

    void "getHeaders for PodController returns a map with 4 elements"() {
        given:
        ControllerReader reader = new ControllerReader(PodController.asType(GrailsClass))

        and:
        headerMock.description() >> "desc"
        headerMock.name() >> "name"
        headerMock.required() >> true
        headerMock.sampleValue() >> "sample"

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
        "desc" == m[0].description
        "name" == m[0].name
        m[0].required
        "sample" == m[0].sampleValue
    }

    void "getHeaders for PodController returns a map with null elements if annotation is not complete"() {
        given:
        ControllerReader reader = new ControllerReader(PodController.asType(GrailsClass))

        and:
        headerMock.description() >> "desc"
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
        "desc" == m[0].description
        "name" == m[0].name
        m[0].required
        !m[0].sampleValue
    }

    void "getHeaders for PodController returns an empty map if annotation has no values"() {
        given:
        ControllerReader reader = new ControllerReader(PodController.asType(GrailsClass))

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

    void "if reader returns nothing, getHeaders returns an ampty map"() {
        given:
        ControllerReader reader = new ControllerReader(PodController.asType(GrailsClass))

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
        ControllerReader reader = new ControllerReader(PodController.asType(GrailsClass))

        and:
        headersMock.value() >> []

        and:
        metadataReaderMock.getAnnotation(_) >> metadataReaderMock
        metadataReaderMock.inController(_) >> controllerMock

        reader.metadataReader = metadataReaderMock

        when:
        def m = reader.controllerIsSupported

        then:
        m
    }

    void "isSupported returns false for OtherController if metadata reader returns null"() {
        given:
        ControllerReader reader = new ControllerReader(OtherController.asType(GrailsClass))

        and:
        headersMock.value() >> []

        and:
        metadataReaderMock.getAnnotation(_) >> metadataReaderMock
        metadataReaderMock.inController(_) >> null

        reader.metadataReader = metadataReaderMock

        when:
        def m = reader.controllerIsSupported

        then:
        !m
    }
}
