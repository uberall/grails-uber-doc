class UberdocGrailsPlugin {
    def version = "1.0.0"
    def grailsVersion = "2.4 > *"
    def pluginExcludes = [
            "grails-app/views/**",
            "grails-app/conf/**",
            "grails-app/controllers/**",
            "grails-app/domain/**",
            "src/groovy/sample/**",
            "test/**"
    ]

    def title = "UberDoc - Rest-API Documentation" // Headline display name of the plugin
    def author = "Felipe Carvalho"
    def authorEmail = "felipe@uberall.com"
    def description = '''\
uberDoc is a very simple solution for creating API documentation based on annotations in domain objects and controllers, and Grails' message system.

It goes through controllers, finds resources, fetches all used objects, and extracts all parameters, headers, errors, etc.

For more documentation, see our GitHub repository.
'''

    def documentation = "https://github.com/uberall/grails-uber-doc"
    def license = "BSD"
    def organization = [name: "uberall", url: "https://uberall.com/"]
    def developers = [[name: "Florian Langenhahn", email: "florian.langenhahn@uberall.com"], [name: "Florian Huebner", email: "florian@uberall.com"]]
    def issueManagement = [system: "GitHub", url: "https://github.com/uberall/grails-uber-doc/issues"]
    def scm = [url: "https://github.com/uberall/grails-uber-doc/"]

}
