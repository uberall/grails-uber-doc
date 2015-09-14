package sample

import uberdoc.annotation.UberDocExplicitProperties
import uberdoc.annotation.UberDocExplicitProperty
import uberdoc.annotation.UberDocModel
import uberdoc.annotation.UberDocProperty

@UberDocModel
@UberDocExplicitProperties([
        @UberDocExplicitProperty(name = "inherited", type = String),
        @UberDocExplicitProperty(name = "id", type = Long)
])
@UberDocExplicitProperty(name = "dateCreated", type = Date)
class Pod extends AbstractObject {

    @UberDocProperty(required = true)
    String license

    @UberDocProperty
    String botName

    static hasOne = [jedi: Persona]

    static constraints = {
        license blank: true, nullable: false
        botName validator: { val, obj ->
            if (!val) {
                return false
            }
        }
    }
}
