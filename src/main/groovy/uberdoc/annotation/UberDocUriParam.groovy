package uberdoc.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Provides information about URI parameters used by a resource (e.g.: /api/dogs/{id}/children/{otherId}/).
 */
@Target([ElementType.METHOD, ElementType.TYPE])
@Retention(RetentionPolicy.RUNTIME)
@interface UberDocUriParam {

    /**
     * Name of the URI parameter, e.g.: "id".
     * This name is used for positional binding and replacing of UrlMappings parameters when generating documentation. For instance,
     * if a given resource's URI is defined as /api/resource/{}/subresource/{}, the {}'s will be replaced for "id" and "otherId" when
     * generating this resource's documentation.
     */
    String name()

    /**
     * Describe the URI param usage. E.g.: {id} identifies the location for which the sync is started.
     */
    String description() default ""

    /**
     * A sample valid value for the header, for illustration purposes. E.g.: 4
     */
    String sampleValue() default ""

}
