package uberdoc.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * The sole purpose of this annotation is to allow the usage of a collection of {@link UberDocQueryParam}.
 */
@Target([ElementType.METHOD, ElementType.TYPE])
@Retention(RetentionPolicy.RUNTIME)
@interface UberDocQueryParams {
    UberDocQueryParam[] value()
}
