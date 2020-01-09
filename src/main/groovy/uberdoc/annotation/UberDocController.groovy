package uberdoc.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Identifies a controller which information should be made public on the API. If a given controller is meant to serve
 * only as an internal API of your app, just don't use this annotation and the information about its resources won't be
 * made available in API docs.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface UberDocController {
    /**
     * Flag indicating that the controller is for internal use only (documentation should not be published to the public docs)
     */
    boolean internalOnly() default false
}