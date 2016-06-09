package uberdoc.metadata

import org.codehaus.groovy.grails.commons.GrailsClass

import java.lang.reflect.Method

/**
 * General purpose class, internally reused to obtain metadata information from classes.
 */
class MetadataReader {

    private type

    MetadataReader getAnnotation(t) {
        type = t
        return this
    }

    def inClass(GrailsClass object) {
        return object.annotations.find { it.annotationType() == type }
    }

    def inController(GrailsClass controller) {
        return controller.clazz.annotations.find { it.annotationType() == type }
    }

    def inMethod(Method method) {
        return method.annotations.find { it.annotationType() == type }
    }

}
