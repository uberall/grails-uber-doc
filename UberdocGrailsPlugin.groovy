class UberdocGrailsPlugin {

    // the plugin version
    def version = "0.1.4-SNAPSHOT"

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.4 > *"

    // resources that are excluded from plugin packaging
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

    // URL to the plugin's documentation
    def documentation = "https://github.com/uberall/grails-uber-doc"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "BSD"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "uberall GmbH", url: "https://uberall.com/" ]

    // Any additional developers beyond the author specified above.
    def developers = [ [name: "Florian Langenhahn", email: "florian.langenhahn@uberall.com"] ]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
