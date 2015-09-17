package sample

import uberdoc.annotation.*

@UberDocController
class PodController { // this example simulates a CRUD-ish controller

    @UberDocResource(responseObject = Pod, responseIsCollection = true)
    @UberDocError(errorCode = "NF404", httpCode = 404)
    @UberDocUriParam(name = "id")
    def get() {}

    @UberDocHeader(name = "hdr")
    @UberDocResource(responseObject = Pod, responseIsCollection = true, title = "custom title for list resource", description = "custom description for list resource")
    @UberDocQueryParams([
            @UberDocQueryParam(name = "page", required = false, description = "custom description", sampleValue = "custom value")
    ])
    @UberDocQueryParam(name = "max", required = true)
    def list() {}

    @UberDocResource(object = Pod)
    @UberDocError(errorCode = "NF404", httpCode = 404, description = "my sample error")
    @UberDocHeader(name = "some header param", description = "my header", sampleValue = "my sample value")
    @UberDocUriParams([
            @UberDocUriParam(name = "firstId"),
            @UberDocUriParam(name = "secondId")
    ])
    @UberDocUriParam(name = "thirdId")
    @UberDocBodyParams([
            @UberDocBodyParam(name = "firstBody", type = Long),
            @UberDocBodyParam(name = "secondBody", description = "2nd body desc", sampleValue = "body", required = false)
    ])
    def create() {}

    @UberDocHeader(name = "hdr")
    @UberDocResource(requestObject = Pod, responseObject = Pod)
    @UberDocUriParam(name = "id", description = "custom description for id", sampleValue = "custom sample value for id")
    @UberDocQueryParam(name = "foobar")
    def update() {}

    @UberDocResource()
    @UberDocUriParam(name = "id")
    def delete() {}

    @UberDocHeader(name = "never_reached")
    def foobar() {}
}
