package sample

import uberdoc.annotation.UberDocProperty

/**
 * Created by felipe on 22/06/15.
 */
abstract class AbstractObject {

    @UberDocProperty(description = "shared is used for ...", sampleValue = "sherd")
    String shared

    String something
}
