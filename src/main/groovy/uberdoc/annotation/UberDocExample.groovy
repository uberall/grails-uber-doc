package uberdoc.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Provides example information as to the use and response of an action.
 */
@Target([ElementType.METHOD, ElementType.TYPE])
@Retention(RetentionPolicy.RUNTIME)
@interface UberDocExample {
    /**
     * The .json file containing the example(s)
     * @return
     */
    String file() default ""
}