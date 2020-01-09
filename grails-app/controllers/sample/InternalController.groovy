package sample

import uberdoc.annotation.UberDocController
import uberdoc.annotation.UberDocError
import uberdoc.annotation.UberDocQueryParam
import uberdoc.annotation.UberDocQueryParams
import uberdoc.annotation.UberDocResource
import uberdoc.annotation.UberDocUriParam

@UberDocController(internalOnly = true)
class InternalController {

    @UberDocResource(title = "Get Internal", description = "Gets an internal resource by id", responseObject = Internal, responseIsCollection = true)
    @UberDocError(errorCode = "NF404", httpCode = 404)
    @UberDocUriParam(name = "id")
    def get(long id) {}

    @UberDocResource(responseObject = Internal, responseIsCollection = true, title = "List Internals", description = "Gets a list of internal resources")
    @UberDocQueryParams([
            @UberDocQueryParam(name = "page", required = false, description = "custom description", sampleValue = "custom value"),
            @UberDocQueryParam(name = "max", required = true)
    ])
    def list() {}
}
