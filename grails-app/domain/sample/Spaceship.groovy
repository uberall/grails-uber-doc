package sample

import uberdoc.annotation.UberDocExplicitProperty
import uberdoc.annotation.UberDocModel

@UberDocModel(description = "overriden description for Spaceship")
@UberDocExplicitProperty(name = "dateCreated", type = Date)
class Spaceship {

    String name
    String shipData
    Planet planet

    static hasOne = [captain: Persona]
}
