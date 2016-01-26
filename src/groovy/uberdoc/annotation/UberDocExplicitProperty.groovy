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

    /**
     * Defines if the property is a collection of the declared type (e.g.: type = String and isCollection = true means it's a Collection<String>)
     */
    boolean isCollection() default false

    /**
     * An example value of a request/response attribute.
     */
    String sampleValue() default ""

    /**
     * Describes the attribute, its purpose, custom validations, etc.
     */
    String description() default ""
}
