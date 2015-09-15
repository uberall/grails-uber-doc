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

This plugin offers basically a set of annotations, along with plural variations of some of them, when applicable:

##### @UberDocController:
Identifies a controller that defines API resources that should be documented.
##### @UberDocResource:
Identifies a controller method (aka resource) to be documented. Within an `UberDocController`, if a given method on a controller is not meant to have its documentation made available, just don't annotate it. Parameters are `requestObject`, `responseObject`, `requestIsCollection`, and `responseIsCollection`.
##### @UberDocError(s):
Provides information about errors a resource can signal to the user. Parameters are `httpCode` and `errorCode`.
##### @UberDocHeader(s):
Provides information about headers to be used when interacting with a given resource. Parameters are `name` and `required`.
##### @UberDocUriParam(s):
Provides information about URI parameters used by a resource (e.g.: /api/dogs/$id/children/$otherId/). Parameters are `name` and `required`. `name` will be used for displaying about a specific parameter in the URI. Note that this is positional, meaning, an annotation about $id should be used before an annotation about $otherId to be applied to the resource's URI correctly.
##### @UberDocQueryParam(s):
Documents information about query parameters used in a resources URL. For instance, it can be used to document pagination parameters within a URL like /api/dogs/?max=10&offset=3. Parameters are `name` and `required`.
##### @UberDocBodyParam(s):
Documents information about parameters that have to be sent as part of the body. For instance, it can be used to describe a subset of parameters (excluding auto-generated properties) used to create a response object.
##### @UberDocModel:
Describes classes used as request or response objects.
##### @UberDocProperty:
Defines information about properties of a given request/response object to be made available in the API documentation. Please specify hasMany collections explicitly as Set<Object> / List<Object> and annotate them.
##### @UberDocExplicitProperty(ies):
Allows describing additional properties explicitly. Such could be values calculated for response objects when marshalling JSON responses for them. Parameters are `name` and `type`.

Again, resources descriptions will be retrieved from regular message bundles, using proper locales. The keys are structured with regard to the resources / objects, parameters and http methods, e.g., `uberDoc.resource.your.path.$someId.PATCH.uriParam.someId.name`.

Just annotate the proper classes / methods, inject UberDocService and play around with getApiDocs. All of the rest (caching, UI) can be implemented in the way that fits you best :)


###### Made with <3 in Berlin
