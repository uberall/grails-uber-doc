package uberdoc.metadata

import org.codehaus.groovy.grails.commons.GrailsClass
import uberdoc.annotation.UberDocController
import uberdoc.annotation.UberDocHeader
import uberdoc.annotation.UberDocHeaders
import uberdoc.messages.MessageReader

/**
 * This class is responsible for reading Grails controllers metadata, such as controller level annotations.
 */
class ControllerReader {

    GrailsClass controller
    MetadataReader metadataReader
    def messageSource
    MessageReader messageReader

    ControllerReader(GrailsClass gClass, def ms) {
        controller = gClass
        metadataReader = new MetadataReader()
        messageSource = ms

        messageReader = new MessageReader(messageSource, Locale.default)
    }

    MethodReader useLocale(def l){
        if(l){
            locale = l
            messageReader = new MessageReader(messageSource, locale)
        }
        return this
    }

    boolean isControllerSupported(){
        return metadataReader.getAnnotation(UberDocController).inController(controller)
    }

    List<Map> getHeaders(){
        UberDocHeaders headers = metadataReader.getAnnotation(UberDocHeaders).inController(controller)
        def ret = []

        if(!headers){
            return []
        }

        headers.value().each { UberDocHeader hdr ->
            String description = messageReader.get("uberDoc.object.${controller.class.simpleName.replace("Controller", "")}.genericHeader.${hdr.name()}.description")
            String sampleValue = messageReader.get("uberDoc.object.${controller.class.simpleName.replace("Controller", "")}.genericHeader.${hdr.name()}.sampleValue")
            ret << [name: hdr.name(), required: hdr.required(), description: description, sampleValue: sampleValue]
        }

        return ret
    }
}
