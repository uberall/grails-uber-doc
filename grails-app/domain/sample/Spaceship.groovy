package sample

class Spaceship {

    String name
    String shipData
    Planet planet

    static hasOne = [captain: Persona]
}
