package uberdoc.metadata

import grails.core.GrailsClass
import sample.OtherController
import sample.PodController
import spock.lang.Specification
import uberdoc.annotation.UberDocController
import uberdoc.annotation.UberDocError

class MetadataReaderSpec extends Specification {

    private MetadataReader reader = new MetadataReader()

    void "getAnnotation for PodController and annotation Error returns no instances"() {
        when:
        def annotation = reader.getAnnotation(UberDocError).inClass(PodController as GrailsClass)

        then:
        !annotation
    }

    void "getAnnotation for PodController and annotation UberDocController returns an instance"() {
        when:
        def annotation = reader.getAnnotation(UberDocController).inClass(PodController as GrailsClass)

        then:
        annotation
    }

    void "getAnnotation for OtherController and annotation UberDocController returns null"() {
        when:
        def annotation = reader.getAnnotation(UberDocController).inClass(OtherController as GrailsClass)

        then:
        !annotation
    }
}
