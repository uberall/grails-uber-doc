# Uber Doc Grails Plugin

A very simple Grails plugin to generate RESTful API documentation.

*Find older readme files [here](/readme_files)*

This plug-in is meant to provide a very simple way to extract RESTful information from Grails apps.

What it does is going through all controllers annotated with @UberDocController and extract information from request/response objects, along with information from UrlMappings
and structure it within an ApiDocumentation object, that then can be used whatever way the app using it feels like is more appropriate.

So, the plugin provides:
- a set of annotations
- a Service with a public method that extracts information from classes and return it as an ApiDocumentation object
- ApiDocumentation object which contains following properties
-- objects (Map): 
-- resources (List<Map>)
-- resourcesByObjects(List<Map.Entry>)

## Installation

Edit build.gradle, by adding the following:

```groovy
repositories {
    ...
    maven { url "http://dl.bintray.com/uberall/plugins" }
    ...
}

dependencies {
    ...
    compile 'org.grails.plugins:uber-doc:4.0.1'
    ...
}
```

## How does it work?

As previously stated, this plugin offers a Service class (UberDocService) with a public method (getApiDocs). When invoked, this method will

- go through every controller
- skip the ones not annotated with @UberDocController and
- for each controller that uses @UberDocController, it will
-- retrieve all mappings associated with this controller in UrlMappings
-- retrieve all methods from that controller annotated with @UberDocResource
-- combine the information extracted from UrlMappings with the information extracted from both controller and methods annotation to build a block of meta information about both the resource served by that method and the objects used in request and response messages.
- creates new ApiDocumentation object and sort it by following before returning it
-- objects: all objects will be sorted (asc) by their 'name', while for each object the 'properties" will also be sorted (asc) by their name
-- resources: for each resources Map the following fields will be sorted (asc) by their name, 'queryParams', 'uriParams' and 'bodyParams'
-- resources itself will be sorted (asc) by their 'uri' and 'method' where method will be replaced with their associated orderingNumber (GET:0, POST:1, PUT:2, PATCH:3, DELETE:4, default:99)

Examples of information about API resources that are returned by this method:

- resources URI's (e.g.: /api/spaceships)
- supported methods (e.g.: /api/spaceships supports GET and POST)
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
Identifies a controller method (aka resource) to be documented. Within an `UberDocController`. Parameters are `requestObject`, `responseObject`, `requestIsCollection`, and `responseIsCollection`.
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

##### @UberDocExample:
Attaches examples from a json file to the method.

Usage: 

```
@UberDocExample(file = <filename>)
```
where `filename` is the name of a json file under the `resources` directory.`

Example json:

```
{
   "examples":
   [
      {
         "name": "Some example",
         "query_params": {
            "lang": "en",
            "version": "20181010"
         },
         "body": {
            "businessId": 123,
            "locationId": 456
         },
         "response": {
            "statusCode": 200,
            "output": {
               "message": "Example message"
            }
         }
      }
   ]
}
```

## Example usages

You can find examples of the plugin usage within the source code itself, under the "sample" package:

https://github.com/uberall/grails-uber-doc/tree/master/grails-app/controllers/sample
https://github.com/uberall/grails-uber-doc/tree/master/grails-app/domain/sample

Once the annotations have been added to your code, a call to `UberDocService#getApiDocs` will return an ApiDocumentation object with the following three properties: 

* `resources`: contains information about the resources supported by your app. These information include supported HTTP methods (derived from `UrlMappings.groovy`), supported headers, possible errors (derived from the annotations you put on your controllers).
* `resourcesByObjects`: collected resource information grouped by their URI name (e.g. [["key": "pods", "value": [...]], ["key": "Spaceship", "value": [...]]])
* `objects`: holds information about the objects your app uses for transiting information. This derives from the annotations you put on your controllers and annotations put on your domain / POGO classes. That includes attributes contained on each object, their types (`String`, `Long`, etc) and constraints (e.g.: `maxSize: 20`, `nullable: false`), among other things.

For example, if you run the integration test provided with the plugin, `UberDocService#getApiDocs` will return an ApiDocumentation object as the following (using JSON representation):

```
{
    "resources": [
        {
            "baseMessageKey": "uberDoc.resource.api.pods.GET",
            "title": "custom title for list resource",
            "description": "custom description for list resource",
            "uri": "/api/pods",
            "method": "GET",
            "requestObject": null,
            "requestCollection": false,
            "responseObject": "Pod",
            "responseCollection": true,
            "uriParams": [],
            "queryParams": [
                {
                    "name": "max",
                    "description": "uberDoc.resource.api.pods.GET.queryParam.max.description",
                    "sampleValue": "uberDoc.resource.api.pods.GET.queryParam.max.sampleValue",
                    "required": true
                },
                {
                    "name": "page",
                    "description": "custom description",
                    "sampleValue": "custom value",
                    "required": false
                }
            ],
            "bodyParams": [],
            "headers": [
                {
                    "name": "hdr",
                    "description": "uberDoc.resource.api.pods.GET.header.hdr.description",
                    "sampleValue": "uberDoc.resource.api.pods.GET.header.hdr.sampleValue",
                    "required": false
                }
            ],
            "errors": [],
            "examples": null,
            "internalOnly": false
        },
        {
            "baseMessageKey": "uberDoc.resource.api.pods.POST",
            "title": "uberDoc.api.pods.POST.title",
            "description": "uberDoc.resource.api.pods.POST.description",
            "uri": "/api/pods",
            "method": "POST",
            "requestObject": "Pod",
            "requestCollection": false,
            "responseObject": "Pod",
            "responseCollection": false,
            "uriParams": [
                {
                    "name": "firstId",
                    "description": "uberDoc.resource.api.pods.POST.uriParam.firstId.description",
                    "sampleValue": "uberDoc.resource.api.pods.POST.uriParam.firstId.sampleValue"
                },
                {
                    "name": "secondId",
                    "description": "uberDoc.resource.api.pods.POST.uriParam.secondId.description",
                    "sampleValue": "uberDoc.resource.api.pods.POST.uriParam.secondId.sampleValue"
                },
                {
                    "name": "thirdId",
                    "description": "uberDoc.resource.api.pods.POST.uriParam.thirdId.description",
                    "sampleValue": "uberDoc.resource.api.pods.POST.uriParam.thirdId.sampleValue"
                }
            ],
            "queryParams": [],
            "bodyParams": [
                {
                    "name": "firstBody",
                    "description": "uberDoc.resource.api.pods.POST.bodyParam.firstBody.description",
                    "sampleValue": "uberDoc.resource.api.pods.POST.bodyParam.firstBody.sampleValue",
                    "type": "Long",
                    "required": true
                },
                {
                    "name": "secondBody",
                    "description": "2nd body desc",
                    "sampleValue": "body",
                    "type": "String",
                    "required": false
                }
            ],
            "headers": [
                {
                    "name": "some header param",
                    "description": "my header",
                    "sampleValue": "my sample value",
                    "required": false
                }
            ],
            "errors": [
                {
                    "errorCode": "NF404",
                    "httpCode": 404,
                    "description": "my sample error"
                }
            ],
            "examples": null,
            "internalOnly": false
        },
        {
            "baseMessageKey": "uberDoc.resource.api.pods.$id.GET",
            "title": "uberDoc.api.pods.$id.GET.title",
            "description": "uberDoc.resource.api.pods.$id.GET.description",
            "uri": "/api/pods/$id",
            "method": "GET",
            "requestObject": null,
            "requestCollection": false,
            "responseObject": "Pod",
            "responseCollection": true,
            "uriParams": [
                {
                    "name": "id",
                    "description": "uberDoc.resource.api.pods.$id.GET.uriParam.id.description",
                    "sampleValue": "uberDoc.resource.api.pods.$id.GET.uriParam.id.sampleValue"
                }
            ],
            "queryParams": [],
            "bodyParams": [],
            "headers": [],
            "errors": [
                {
                    "errorCode": "NF404",
                    "httpCode": 404,
                    "description": "uberDoc.resource.api.pods.$id.GET.error.404.description"
                }
            ],
            "examples": null,
            "internalOnly": false
        },
        {
            "baseMessageKey": "uberDoc.resource.api.pods.$id.PUT",
            "title": "uberDoc.api.pods.$id.PUT.title",
            "description": "uberDoc.resource.api.pods.$id.PUT.description",
            "uri": "/api/pods/$id",
            "method": "PUT",
            "requestObject": "Pod",
            "requestCollection": false,
            "responseObject": "Pod",
            "responseCollection": false,
            "uriParams": [
                {
                    "name": "id",
                    "description": "custom description for id",
                    "sampleValue": "custom sample value for id"
                }
            ],
            "queryParams": [
                {
                    "name": "foobar",
                    "description": "uberDoc.resource.api.pods.$id.PUT.queryParam.foobar.description",
                    "sampleValue": "uberDoc.resource.api.pods.$id.PUT.queryParam.foobar.sampleValue",
                    "required": false
                }
            ],
            "bodyParams": [],
            "headers": [
                {
                    "name": "hdr",
                    "description": "uberDoc.resource.api.pods.$id.PUT.header.hdr.description",
                    "sampleValue": "uberDoc.resource.api.pods.$id.PUT.header.hdr.sampleValue",
                    "required": false
                }
            ],
            "errors": [],
            "examples": null,
            "internalOnly": false
        },
        {
            "baseMessageKey": "uberDoc.resource.api.pods.$id.DELETE",
            "title": "uberDoc.api.pods.$id.DELETE.title",
            "description": "uberDoc.resource.api.pods.$id.DELETE.description",
            "uri": "/api/pods/$id",
            "method": "DELETE",
            "requestObject": null,
            "requestCollection": false,
            "responseObject": null,
            "responseCollection": false,
            "uriParams": [
                {
                    "name": "id",
                    "description": "uberDoc.resource.api.pods.$id.DELETE.uriParam.id.description",
                    "sampleValue": "uberDoc.resource.api.pods.$id.DELETE.uriParam.id.sampleValue"
                }
            ],
            "queryParams": [],
            "bodyParams": [],
            "headers": [],
            "errors": [],
            "examples": null,
            "internalOnly": false
        },
        {
            "baseMessageKey": "uberDoc.resource.api.something.else.POST",
            "title": "uberDoc.api.something.else.POST.title",
            "description": "uberDoc.resource.api.something.else.POST.description",
            "uri": "/api/something/else",
            "method": "POST",
            "requestObject": "Pod",
            "requestCollection": false,
            "responseObject": "Pod",
            "responseCollection": false,
            "uriParams": [
                {
                    "name": "firstId",
                    "description": "uberDoc.resource.api.something.else.POST.uriParam.firstId.description",
                    "sampleValue": "uberDoc.resource.api.something.else.POST.uriParam.firstId.sampleValue"
                },
                {
                    "name": "secondId",
                    "description": "uberDoc.resource.api.something.else.POST.uriParam.secondId.description",
                    "sampleValue": "uberDoc.resource.api.something.else.POST.uriParam.secondId.sampleValue"
                },
                {
                    "name": "thirdId",
                    "description": "uberDoc.resource.api.something.else.POST.uriParam.thirdId.description",
                    "sampleValue": "uberDoc.resource.api.something.else.POST.uriParam.thirdId.sampleValue"
                }
            ],
            "queryParams": [],
            "bodyParams": [],
            "headers": [
                {
                    "name": "some header param",
                    "description": "uberDoc.resource.api.something.else.POST.header.some.header.param.description",
                    "sampleValue": "uberDoc.resource.api.something.else.POST.header.some.header.param.sampleValue",
                    "required": false
                }
            ],
            "errors": [],
            "examples": {
                "examples": [
                    {
                        "name": "Some example",
                        "query_params": {
                            "lang": "en",
                            "version": "20181010"
                        },
                        "body": {
                            "businessId": 123,
                            "locationId": 456
                        },
                        "response": {
                            "statusCode": 200,
                            "output": {
                                "message": "Example message"
                            }
                        }
                    }
                ]
            },
            "internalOnly": false
        }
    ],
    "resourcesByObjects": [
        {
            "key": "pods",
            "value": [
                {
                    "baseMessageKey": "uberDoc.resource.api.pods.GET",
                    "title": "custom title for list resource",
                    "description": "custom description for list resource",
                    "uri": "/api/pods",
                    "method": "GET",
                    "requestObject": null,
                    "requestCollection": false,
                    "responseObject": "Pod",
                    "responseCollection": true,
                    "uriParams": [],
                    "queryParams": [
                        {
                            "name": "max",
                            "description": "uberDoc.resource.api.pods.GET.queryParam.max.description",
                            "sampleValue": "uberDoc.resource.api.pods.GET.queryParam.max.sampleValue",
                            "required": true
                        },
                        {
                            "name": "page",
                            "description": "custom description",
                            "sampleValue": "custom value",
                            "required": false
                        }
                    ],
                    "bodyParams": [],
                    "headers": [
                        {
                            "name": "hdr",
                            "description": "uberDoc.resource.api.pods.GET.header.hdr.description",
                            "sampleValue": "uberDoc.resource.api.pods.GET.header.hdr.sampleValue",
                            "required": false
                        }
                    ],
                    "errors": [],
                    "examples": null,
                    "internalOnly": false
                },
                {
                    "baseMessageKey": "uberDoc.resource.api.pods.POST",
                    "title": "uberDoc.api.pods.POST.title",
                    "description": "uberDoc.resource.api.pods.POST.description",
                    "uri": "/api/pods",
                    "method": "POST",
                    "requestObject": "Pod",
                    "requestCollection": false,
                    "responseObject": "Pod",
                    "responseCollection": false,
                    "uriParams": [
                        {
                            "name": "firstId",
                            "description": "uberDoc.resource.api.pods.POST.uriParam.firstId.description",
                            "sampleValue": "uberDoc.resource.api.pods.POST.uriParam.firstId.sampleValue"
                        },
                        {
                            "name": "secondId",
                            "description": "uberDoc.resource.api.pods.POST.uriParam.secondId.description",
                            "sampleValue": "uberDoc.resource.api.pods.POST.uriParam.secondId.sampleValue"
                        },
                        {
                            "name": "thirdId",
                            "description": "uberDoc.resource.api.pods.POST.uriParam.thirdId.description",
                            "sampleValue": "uberDoc.resource.api.pods.POST.uriParam.thirdId.sampleValue"
                        }
                    ],
                    "queryParams": [],
                    "bodyParams": [
                        {
                            "name": "firstBody",
                            "description": "uberDoc.resource.api.pods.POST.bodyParam.firstBody.description",
                            "sampleValue": "uberDoc.resource.api.pods.POST.bodyParam.firstBody.sampleValue",
                            "type": "Long",
                            "required": true
                        },
                        {
                            "name": "secondBody",
                            "description": "2nd body desc",
                            "sampleValue": "body",
                            "type": "String",
                            "required": false
                        }
                    ],
                    "headers": [
                        {
                            "name": "some header param",
                            "description": "my header",
                            "sampleValue": "my sample value",
                            "required": false
                        }
                    ],
                    "errors": [
                        {
                            "errorCode": "NF404",
                            "httpCode": 404,
                            "description": "my sample error"
                        }
                    ],
                    "examples": null,
                    "internalOnly": false
                },
                {
                    "baseMessageKey": "uberDoc.resource.api.pods.$id.GET",
                    "title": "uberDoc.api.pods.$id.GET.title",
                    "description": "uberDoc.resource.api.pods.$id.GET.description",
                    "uri": "/api/pods/$id",
                    "method": "GET",
                    "requestObject": null,
                    "requestCollection": false,
                    "responseObject": "Pod",
                    "responseCollection": true,
                    "uriParams": [
                        {
                            "name": "id",
                            "description": "uberDoc.resource.api.pods.$id.GET.uriParam.id.description",
                            "sampleValue": "uberDoc.resource.api.pods.$id.GET.uriParam.id.sampleValue"
                        }
                    ],
                    "queryParams": [],
                    "bodyParams": [],
                    "headers": [],
                    "errors": [
                        {
                            "errorCode": "NF404",
                            "httpCode": 404,
                            "description": "uberDoc.resource.api.pods.$id.GET.error.404.description"
                        }
                    ],
                    "examples": null,
                    "internalOnly": false
                },
                {
                    "baseMessageKey": "uberDoc.resource.api.pods.$id.PUT",
                    "title": "uberDoc.api.pods.$id.PUT.title",
                    "description": "uberDoc.resource.api.pods.$id.PUT.description",
                    "uri": "/api/pods/$id",
                    "method": "PUT",
                    "requestObject": "Pod",
                    "requestCollection": false,
                    "responseObject": "Pod",
                    "responseCollection": false,
                    "uriParams": [
                        {
                            "name": "id",
                            "description": "custom description for id",
                            "sampleValue": "custom sample value for id"
                        }
                    ],
                    "queryParams": [
                        {
                            "name": "foobar",
                            "description": "uberDoc.resource.api.pods.$id.PUT.queryParam.foobar.description",
                            "sampleValue": "uberDoc.resource.api.pods.$id.PUT.queryParam.foobar.sampleValue",
                            "required": false
                        }
                    ],
                    "bodyParams": [],
                    "headers": [
                        {
                            "name": "hdr",
                            "description": "uberDoc.resource.api.pods.$id.PUT.header.hdr.description",
                            "sampleValue": "uberDoc.resource.api.pods.$id.PUT.header.hdr.sampleValue",
                            "required": false
                        }
                    ],
                    "errors": [],
                    "examples": null,
                    "internalOnly": false
                },
                {
                    "baseMessageKey": "uberDoc.resource.api.pods.$id.DELETE",
                    "title": "uberDoc.api.pods.$id.DELETE.title",
                    "description": "uberDoc.resource.api.pods.$id.DELETE.description",
                    "uri": "/api/pods/$id",
                    "method": "DELETE",
                    "requestObject": null,
                    "requestCollection": false,
                    "responseObject": null,
                    "responseCollection": false,
                    "uriParams": [
                        {
                            "name": "id",
                            "description": "uberDoc.resource.api.pods.$id.DELETE.uriParam.id.description",
                            "sampleValue": "uberDoc.resource.api.pods.$id.DELETE.uriParam.id.sampleValue"
                        }
                    ],
                    "queryParams": [],
                    "bodyParams": [],
                    "headers": [],
                    "errors": [],
                    "examples": null,
                    "internalOnly": false
                }
            ]
        },
        {
            "key": "something",
            "value": [
                {
                    "baseMessageKey": "uberDoc.resource.api.something.else.POST",
                    "title": "uberDoc.api.something.else.POST.title",
                    "description": "uberDoc.resource.api.something.else.POST.description",
                    "uri": "/api/something/else",
                    "method": "POST",
                    "requestObject": "Pod",
                    "requestCollection": false,
                    "responseObject": "Pod",
                    "responseCollection": false,
                    "uriParams": [
                        {
                            "name": "firstId",
                            "description": "uberDoc.resource.api.something.else.POST.uriParam.firstId.description",
                            "sampleValue": "uberDoc.resource.api.something.else.POST.uriParam.firstId.sampleValue"
                        },
                        {
                            "name": "secondId",
                            "description": "uberDoc.resource.api.something.else.POST.uriParam.secondId.description",
                            "sampleValue": "uberDoc.resource.api.something.else.POST.uriParam.secondId.sampleValue"
                        },
                        {
                            "name": "thirdId",
                            "description": "uberDoc.resource.api.something.else.POST.uriParam.thirdId.description",
                            "sampleValue": "uberDoc.resource.api.something.else.POST.uriParam.thirdId.sampleValue"
                        }
                    ],
                    "queryParams": [],
                    "bodyParams": [],
                    "headers": [
                        {
                            "name": "some header param",
                            "description": "uberDoc.resource.api.something.else.POST.header.some.header.param.description",
                            "sampleValue": "uberDoc.resource.api.something.else.POST.header.some.header.param.sampleValue",
                            "required": false
                        }
                    ],
                    "errors": [],
                    "examples": {
                        "examples": [
                            {
                                "name": "Some example",
                                "query_params": {
                                    "lang": "en",
                                    "version": "20181010"
                                },
                                "body": {
                                    "businessId": 123,
                                    "locationId": 456
                                },
                                "response": {
                                    "statusCode": 200,
                                    "output": {
                                        "message": "Example message"
                                    }
                                }
                            }
                        ]
                    },
                    "internalOnly": false
                }
            ]
        }
    ],
    "objects": {
        "Persona": {
            "name": "Persona",
            "description": "overriden description for Persona",
            "properties": [
                {
                    "name": "dateCreated",
                    "type": "Date",
                    "description": "uberDoc.object.Persona.dateCreated.description",
                    "sampleValue": "uberDoc.object.Persona.dateCreated.sampleValue",
                    "required": false,
                    "isCollection": false
                }
            ]
        },
        "Pod": {
            "name": "Pod",
            "description": "overriden description for model",
            "properties": [
                {
                    "name": "botName",
                    "description": "botName has a description",
                    "sampleValue": "botName has a sample value",
                    "required": false,
                    "type": "String",
                    "constraints": [
                        {
                            "constraint": "custom",
                            "value": "uberDoc.object.Pod.constraints.custom"
                        },
                        {
                            "constraint": "nullable",
                            "value": false
                        }
                    ]
                },
                {
                    "name": "dateCreated",
                    "type": "Date",
                    "description": "uberDoc.object.Pod.dateCreated.description",
                    "sampleValue": "uberDoc.object.Pod.dateCreated.sampleValue",
                    "required": false,
                    "isCollection": false
                },
                {
                    "name": "id",
                    "type": "Long",
                    "description": "uberDoc.object.Pod.id.description",
                    "sampleValue": "uberDoc.object.Pod.id.sampleValue",
                    "required": false,
                    "isCollection": false
                },
                {
                    "name": "inherited",
                    "type": "String",
                    "description": "uberDoc.object.Pod.inherited.description",
                    "sampleValue": "uberDoc.object.Pod.inherited.sampleValue",
                    "required": false,
                    "isCollection": false
                },
                {
                    "name": "license",
                    "description": "uberDoc.object.Pod.license.description",
                    "sampleValue": "uberDoc.object.Pod.license.sampleValue",
                    "required": true,
                    "type": "String",
                    "constraints": [
                        {
                            "constraint": "blank",
                            "value": true
                        },
                        {
                            "constraint": "nullable",
                            "value": false
                        }
                    ]
                },
                {
                    "name": "longCollection",
                    "type": "Long",
                    "description": "uberDoc.object.Pod.longCollection.description",
                    "sampleValue": "uberDoc.object.Pod.longCollection.sampleValue",
                    "required": false,
                    "isCollection": true
                },
                {
                    "name": "persons",
                    "description": "uberDoc.object.Pod.persons.description",
                    "sampleValue": "uberDoc.object.Pod.persons.sampleValue",
                    "required": false,
                    "type": "Map",
                    "constraints": [
                        {
                            "constraint": "nullable",
                            "value": true
                        }
                    ]
                },
                {
                    "name": "shared",
                    "description": "uberDoc.object.Pod.shared.description",
                    "sampleValue": "uberDoc.object.Pod.shared.sampleValue",
                    "required": false,
                    "type": "String",
                    "constraints": [
                        {
                            "constraint": "nullable",
                            "value": false
                        }
                    ]
                },
                {
                    "name": "spaceship",
                    "type": "Spaceship",
                    "description": "uberDoc.object.Pod.spaceship.description",
                    "sampleValue": "uberDoc.object.Pod.spaceship.sampleValue",
                    "required": false,
                    "isCollection": false
                }
            ]
        },
        "Spaceship": {
            "name": "Spaceship",
            "description": "overriden description for Spaceship",
            "properties": [
                {
                    "name": "dateCreated",
                    "type": "Date",
                    "description": "uberDoc.object.Spaceship.dateCreated.description",
                    "sampleValue": "uberDoc.object.Spaceship.dateCreated.sampleValue",
                    "required": false,
                    "isCollection": false
                }
            ]
        }
    }
}
```

##### Internal Only documentation:
In order to provide internal only documentation, `internalOnly` is supported by the following annotations:
- @UberDocController
- @UberDocModel
- @UberDocProperty
- @UberDocResource

Example: `@UberDocController(internalOnly = true)`

Internal only documentation can be published by setting `uberdoc.publishInternalOnly` to `true` inside application.yml or application.groovy.


###### Made with <3 in Berlin
