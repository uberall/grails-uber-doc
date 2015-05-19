package sample

class Person {

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
