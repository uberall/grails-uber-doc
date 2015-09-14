package uberdoc.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Provides information about headers to be used when interacting with a given resource / controller method. Just
 * like @UberDocError, when used at controller level, defines documentation to be applied to every method within
 * that controller.
 */
@Target([ElementType.METHOD, ElementType.TYPE])
@Retention(RetentionPolicy.RUNTIME)
@interface UberDocHeader {

    /**
     * Name of the HTTP header to be used when interacting with the resource. E.g.: access_token
     */
    String name()

    /**
     * Indicates whether this header is mandatory for activating the resource.
     */
    boolean required() default true
}