# Uber Doc Grails Plugin

A very simple Grails plugin to generate RESTful API documentation.

This plug-in is meant to provide a very simple way to extract RESTful information from Grails apps.

What it does is going through all controllers annotated with @UberDocController and extract information from request/response objects, along with information from UrlMappings
and structure it within a Map, that then can be used whatever way the app using it feels like is more appropriate.

So, the plugin just provides:
- a set of annotations
- a Service with a public method that extracts information from classes and return it as a Map

This was done on purpose, so that your app can decide on the best way to display or cache this information.

## How does it work?

As previously stated, this plugin offers a Service class (UberDocService) with a public method (getApiDocs). When invoked, this method will

- go through every controller
- skip the ones not annotated with @UberDocController and
- for each controller that uses @UberDocController, it will
-- retrieve all mappings associated with this controller in UrlMappings
-- retrieve all methods from that controller annotated with @UberDocResource
-- combine the information extracted from UrlMappings with the information extracted from both controller and methods annotation to build a block of meta information about both the resource served by that method and the objects used in request and response messages.

Examples of information about API resources that are returned by this method:

- resources URI's (e.g.: /api/spaceships/)
- supported methods (e.g.: /api/spaceships/ supports GET and POST)
- description of a resource
- headers to be used when interacting with a resource
- errors to expect when interacting with a resource

Examples of information about request / response objects returned by this method:
- class name
- properties names
- properties types
- properties constraints (when a request / response object is a domain class or uses @Validateable)
- sample value for a property


## How to use this plugin

This plugin offers basically 8 annotations, along with plural variations of some of them, when applicable:

- @UberDocController: identifies a controller which information should be made public on the API. If a given controller is meant to serve only an internal API of your app, just don't use this annotation and information about its resources won't be made available.
- @UberDocResource: identifies a controller method (aka resource) which information should be made public on the API. If a given method on a controller is not meant to have its documentation made available, just don't annotate it.
- @UberDocError(s): this annotation provides information about errors to be expected when interacting with a given resource / controller method. When used at controller level, defines information about errors to be expected when interacting with every method belonging to that controller.
- @UberDocHeader(s): provides information about headers to be used when interacting with a given resource / controller method. Just like @UberDocError, when used at controller level, defines documentation to be applied to every method within that controller.
- @UberDocUriParam(s): provides information about URI parameters used by a resource (e.g.: /api/dogs/{id}/children/{otherId}/). This annotation has a parameter called "name", which will be used for displaying about a specific parameter in the URI. Note that this is positional, meaning, an annotation about {id} should be used before an annotation about {otherId}, so that information gets displayed in the same order.
- @UberDocQueryParam(s): documents information about query parameters used in a resources URL. For instance, it can be used to document pagination parameters within a URL like /api/dogs/?max=10&offset=3.
- @UberDocModel: describes classes used as request or response objects.
- @UberDocProperty: defines information about properties of a given request/response object to be made available in the API documentation, such as a description and a sample value.
- @UberDocImplicitProperty(ies): allows describing implicit properties (e.g.: id, dateCreated) or properties inherited from superclasses.

Notice you don't need to manually enter textual information in annotations. By default, resources descriptions will be retrieved from regular message bundles, using proper locales. The information entered for description, title and sampleValue in annotations will only be used for overriding purposes. 

Just annotate the proper classes / methods, inject UberDocService and play around with getApiDocs. All of the rest (cacheing, UI) can be implemented in the way that fits you best :)




###### Made with <3 in Berlin