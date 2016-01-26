package sample

import uberdoc.annotation.UberDocExplicitProperty
import uberdoc.annotation.UberDocModel

@UberDocModel(description = "overriden description for Persona")
@UberDocExplicitProperty(name = "dateCreated", type = Date)
class Persona {

    String firstName
    String lastName
    List<String> nickNames

    static transients = ['fullName']
    static hasMany = [nickNames: String, pods: Pod]
    static hasOne = [ship: Spaceship]

    String getFullName() {
        return firstName + " " + lastName
    }
}
