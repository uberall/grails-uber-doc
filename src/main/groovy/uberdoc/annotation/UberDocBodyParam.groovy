package uberdoc.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Documents information about body parameters used in a POST or PATCH request.
 *
 * This can be used for requests where no complete object is available / needs to be sent.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface UberDocBodyParam {

    /**
     * Name of the query parameter. E.g.: offset
     */
    String name()

    /**
     * Indicates whether a query param should be informed when interacting with the API.
     */
    boolean required() default true

    /**
     * The property's type (e.g.: Long)
     */
    Class type() default String

    /**
     * Description of the query parameter. May include information about its usage, situations where it's mandatory, etc.
     */
    String description() default ""

    /**
     * Sample value for this parameter, to be provided for illustration purposes.
     */
    String sampleValue() default ""

}
