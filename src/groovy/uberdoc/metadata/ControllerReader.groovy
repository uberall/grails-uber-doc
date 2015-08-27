package uberdoc.metadata

import uberdoc.annotation.UberDocController
import uberdoc.annotation.UberDocErrors
import uberdoc.annotation.UberDocHeaders
import org.codehaus.groovy.grails.commons.GrailsClass

/**
 * This class is responsible for reading Grails controllers metadata, such as controller level annotations.
 */
class ControllerReader {

    GrailsClass controller
    MetadataReader metadataReader

    ControllerReader(GrailsClass gClass) {
        controller = gClass
        metadataReader = new MetadataReader()
    }

    boolean isControllerSupported(){
        return metadataReader.getAnnotation(UberDocController).inController(controller)
    }

    List<Map> getErrors(){
        def errors = metadataReader.getAnnotation(UberDocErrors).inController(controller)
        def ret = []

        if(!errors){
            return []
        }

        errors.value().each { err ->
            ret << [errorCode: err.errorCode(), httpCode: err.httpCode(), description: err.description()]
        }

        return ret
    }

    List<Map> getHeaders(){
        def headers = metadataReader.getAnnotation(UberDocHeaders).inController(controller)
        def ret = []

        if(!headers){
            return []
        }

        headers.value().each { hdr ->
            ret << [name: hdr.name(), description: hdr.description(), required: hdr.required(), sampleValue: hdr.sampleValue()]
        }

        return ret
    }
}
