package sample

import uberdoc.annotation.UberDocExplicitProperties
import uberdoc.annotation.UberDocExplicitProperty
import uberdoc.annotation.UberDocModel
import uberdoc.annotation.UberDocProperty

@UberDocModel(description = "overriden description for model")
@UberDocExplicitProperties([
        @UberDocExplicitProperty(name = "inherited", type = String),
        @UberDocExplicitProperty(name = "id", type = Long),
        @UberDocExplicitProperty(name = "longCollection", type = Long, isCollection = true),
        @UberDocExplicitProperty(name = "spaceship", description = "The spaceship of the Jedi that owns this pod" , type = Spaceship, isCollection = false)
])
@UberDocExplicitProperty(name = "dateCreated", type = Date)
class Pod extends AbstractObject {

    @UberDocProperty(required = true)
    String license

    @UberDocProperty(description = "botName has a description", sampleValue = "botName has a sample value")
    String botName

    @UberDocProperty()
    Map<String, List<Persona>> persons

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
