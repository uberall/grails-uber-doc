package uberdoc

import grails.core.GrailsApplication
import grails.core.GrailsClass
import grails.web.mapping.UrlMappings
import groovy.util.logging.Log4j
import org.springframework.context.MessageSource
import uberdoc.annotation.UberDocResource
import uberdoc.metadata.GrailsReader
import uberdoc.metadata.MetadataReader
import uberdoc.metadata.RequestAndResponseObjects
import uberdoc.parser.UberDocResourceParser

import java.lang.reflect.Method

/**
 * Parses @UberDoc annotations available in source code and structures it as a map,
 * returned to the invoking component for proper display and rendering.
 *
 * @see RequestAndResponseObjects
 */
@Log4j
class UberDocService {

    GrailsApplication grailsApplication
    UrlMappings grailsUrlMappingsHolder
    MessageSource messageSource

    /**
     * Returns a Map with two root objects/information:
     *
     * 1- resources: contain structured information about all resources, using UrlMappings information as base. The basic idea is to:
     * * go through every controller;
     * * for each controller:
     * ** retrieve url mappings associated with that controller (e.g.: "/api/pods"(controller: 'pod', action: [POST: "create"]))
     * ** retrieve all methods available in that controller (e.g.: create, get, list)
     * ** for each method combine:
     * (i) the API information available in the annotations applied to that annotation (specific headers, specific errors, request objects),
     * (ii) the annotation info available at controller level (generic errors and headers),
     * (iii) information available in url mappings (e.g.: method (POST, GET, PATCH)
     *
     * 2- objects: contains information about all objects used either as request or response objects by all controllers
     *
     */
    ApiDocumentation getApiDocs(Locale locale = Locale.default) {
        RequestAndResponseObjects objects = new RequestAndResponseObjects(grailsApplication, messageSource, locale)
        MetadataReader metadataReader = new MetadataReader()
        GrailsReader grailsReader = new GrailsReader(grailsApplication, grailsUrlMappingsHolder)

        List apiResources = []
        Map apiObjects

        for (GrailsClass controller : grailsReader.controllers) {

            UberDocResourceParser parser = new UberDocResourceParser(messageSource, locale)

            // go over all controllers with uberDoc annotations
            List controllerMethods = grailsReader.getMethodsFrom(controller)
            List<LinkedHashMap> controllerMappings = grailsReader.extractUrlMappingsFor(controller)

            controllerMethods.each { Method method ->
                // match method and mapping
                LinkedHashMap mapping = controllerMappings.find { it.name == method.name }

                // if there is no mapping, there's no point int extracting resources
                if (!mapping) {
                    log.warn("Cannot find mapping for $method -> ignored.")
                    return
                }

                // parse the methods annotations for details about the api resource
                apiResources.addAll(parser.parse(method, mapping))

                // find all request and response objects that are used for this api method, add them to the list of available objects
                objects.extractObjectsInfoFromResource(metadataReader.getAnnotation(UberDocResource).inMethod(method))
            }
        }

        apiObjects = objects.fetch()

        // Thanks to groovy we might have controller actions in here twice
        apiResources = apiResources?.unique()

        return new ApiDocumentation(apiObjects, apiResources)
    }

}
