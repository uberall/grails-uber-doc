package uberdoc

import grails.plugins.Plugin

class UberDocGrailsPlugin extends Plugin {

    def grailsVersion = "3.1 > *"
    def pluginExcludes = [
            "**/sample/**"
    ]

    def title = "UberDoc - Rest-API Documentation"
    def author = "Florian Huebner"
    def authorEmail = "florian@uberall.com"
    def description = '''\
uberDoc is a very simple solution for creating API documentation based on annotations in domain objects and controllers, and Grails' message system.

It goes through controllers, finds resources, fetches all used objects, and extracts all parameters, headers, errors, etc.

For more documentation, see our GitHub repository.
'''

    def documentation = "https://github.com/uberall/grails-uber-doc"
    def license = "BSD"
    def organization = [name: "uberall", url: "https://uberall.com/"]
    def developers = [
            [name: "Florian Langenhahn", email: "florian.langenhahn@uberall.com"],
            [name: "Philipp Eschenbach", email: "philipp@uberall.com"],
            [name: "Alex Pedini", email: "alex.pedini@gmail.com"]]
    def issueManagement = [system: "GitHub", url: "https://github.com/uberall/grails-uber-doc/issues"]
    def scm = [url: "https://github.com/uberall/grails-uber-doc/"]
}
