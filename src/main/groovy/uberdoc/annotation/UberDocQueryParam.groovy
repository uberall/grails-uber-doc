package uberdoc.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Documents information about query parameters used in a resources URL. For instance, it can be used to document
 * pagination parameters within a URL like /api/dogs/?max=10&offset=3.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface UberDocQueryParam {

    /**
     * Name of the query parameter. E.g.: offset
     */
    String name()

    /**
     * Description of the query parameter. May include information about its usage, situations where its mandatory, etc.
     */
    String description() default ""

    /**
     * Indicates whether a query param should be informed when interacting with the API.
     */
    boolean required() default false

    /**
     * Sample value for this parameter, to be provided for illustration purposes.
     */
    String sampleValue() default ""

    /**
     * Indicates whether this parameter accepts a collection of values.
     */
    boolean isCollection() default false

}
