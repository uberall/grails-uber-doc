package sample

import uberdoc.annotation.UberDocImplicitProperties
import uberdoc.annotation.UberDocImplicitProperty
import uberdoc.annotation.UberDocModel
import uberdoc.annotation.UberDocProperty

@UberDocModel(description = "This class does something...")
@UberDocImplicitProperties([
        @UberDocImplicitProperty(name = "inherited", type = String, description = "Just an inherited property"),
        @UberDocImplicitProperty(name = "id", type = Long, description = "Identifies a Pod")
])
@UberDocImplicitProperty(name = "dateCreated", type = Date, description = "When the Pod was created")
class Pod extends AbstractObject {

    @UberDocProperty(description = "license is used for ...", sampleValue = "DBNG3r", required = true)
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
