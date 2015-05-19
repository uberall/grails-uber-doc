package uberdoc.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Describes classes used as request or response objects.
 * @see UberDocResource
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface UberDocModel {

    /**
     * Provides information about the class used as request/response object, such as its purpose, situations where its
     * not mandatory, etc.
     */
    String description()

}