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
    String sampleValue() default ""

    /**
     * Describes the attribute, its purpose, custom validations, etc.
     */
    String description() default ""

    /**
     * Defines whether the property is mandatory.
     */
    boolean required() default false

    /**
     * Flag indicating that the controller is for internal use only (documentation should not be published to the public docs)
     */
    boolean internalOnly() default false
}
