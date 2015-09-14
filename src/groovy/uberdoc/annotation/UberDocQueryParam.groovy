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
     * Indicates whether a query param should be informed when interacting with the API.
     */
    boolean required() default true

}
