package sample

import uberdoc.annotation.UberDocModel
import uberdoc.annotation.UberDocProperty

@UberDocModel(description = "This class does something...")
class Pod {

    @UberDocProperty(description = "license is used for ...", sampleValue = "DBNG3r")
    String license

    @UberDocProperty(description = "botName is used for movies credits ...", sampleValue = "C3PO")
    String botName

    static hasOne = [jedi: Person]

    static constraints = {
        license blank: true, nullable: false
        botName validator: { val, obj ->
            if(!val) {
                return false
            }
        }
    }
}
