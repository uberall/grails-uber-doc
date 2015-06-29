package uberdoc.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface UberDocImplicitProperty {

    /**
     * The implicit property's name (e.g.: "id")
     */
    String name()

    /**
     * The implicit property's type (e.g.: Long)
     */
    Class type() default String

    /**
     * An example value of a request/response attribute.
     */
    String sampleValue() default ""

    /**
     * Describes the attribute, its purpose, custom validations, etc.
     */
    String description()

    /**
     * Defines whether the property is mandatory.
     */
    boolean required() default false
}
