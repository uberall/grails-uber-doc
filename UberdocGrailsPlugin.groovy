class UberdocGrailsPlugin {
    def version = "0.1.8-SNAPSHOT"
    def grailsVersion = "2.4 > *"
    def pluginExcludes = [
        "grails-app/views/**",
        "grails-app/conf/**",
        "grails-app/controllers/**",
        "grails-app/domain/**",
        "src/groovy/sample/**",
        "test/**"
    ]

    def title = "Uber Doc Plugin" // Headline display name of the plugin
    def author = "Felipe Carvalho"
    def authorEmail = "felipe@uberall.com"
    def description = '''\
This plug-in is meant to provide a very simple way to extract RESTful information from Grails apps.

What it does is going through all controllers annotated with @UberDocController and extract information from request/response objects, along with information from UrlMappings
and structure it within a Map, that then can be used whatever way the app using it feels like is more appropriate.

So, the plugin just provides:
- a set of annotations
- a Service with a public method that extracts information from classes and return it as a Map

This was done on purpose, so that your app can decide on the best way to display or cache this information.
'''

    def documentation = "https://github.com/uberall/grails-uber-doc"
    def license = "BSD"
    def organization = [ name: "uberall", url: "https://uberall.com/" ]
    def developers = [ [name: "Florian Langenhahn", email: "florian.langenhahn@uberall.com"] ]
    def issueManagement = [ system: "GitHub", url: "https://github.com/uberall/grails-uber-doc/issues" ]
    def scm = [ url: "https://github.com/uberall/grails-uber-doc/" ]

}
