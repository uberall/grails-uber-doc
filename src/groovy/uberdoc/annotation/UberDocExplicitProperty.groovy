package uberdoc.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface UberDocExplicitProperty {

    /**
     * The implicit property's name (e.g.: "id")
     */
    String name()

    /**
     * The implicit property's type (e.g.: Long)
     */
    Class type() default String

    /**
     * Defines whether the property is mandatory.
     */
    boolean required() default false
}
