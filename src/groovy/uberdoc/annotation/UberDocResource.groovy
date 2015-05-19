package uberdoc.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Identifies a controller method (aka resource) which information should be made public on the API. If a given method
 * on a controller is not meant to have its documentation made available, just don't annotate it.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface UberDocResource {

    /**
     * The RESTful resource description, its purpose, usage, etc.
     * @return
     */
    String description() default ""

    /**
     * Class used as request object. Not mandatory if <code>object</code> param is set.
     */
    Class requestObject() default {}

    /**
     * Class used as response object. Not mandatory if <code>object</code> param is set.
     */
    Class responseObject() default {}

    /**
     * Indicates that the resource returns a collection of this class/
     */
    Class responseCollectionOf() default {}

    /**
     * Class used as both request and resource object.
     */
    Class object() default {} // if requestObject and responseObject are the same, just use this attribute as a shortcut

}