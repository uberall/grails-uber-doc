package uberdoc.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Defines information about properties (attributes) of a given request/response object to be made available in the API documentation.
 *
 * @see UberDocModel
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface UberDocProperty {

    /**
     * An example value of a request/response attribute.
     */
    String sampleValue()

    /**
     * Describes the attribute, its purpose, custom validations, etc.
     */
    String description()

}
