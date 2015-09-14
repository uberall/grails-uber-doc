package sample

import uberdoc.annotation.*

@UberDocHeaders([
        @UberDocHeader(name = "publicToken"),
        @UberDocHeader(name = "other token")
])
@UberDocController
class PodController { // this example simulates a CRUD-ish controller

    @UberDocResource(responseObject = Pod, responseIsCollection = true)
    @UberDocError(errorCode = "NF404", httpCode = 404)
    @UberDocUriParam(name = "id")
    def get() { }

    @UberDocHeader(name = "hdr")
    @UberDocResource(responseObject = Pod, responseIsCollection = true)
    @UberDocQueryParams([
            @UberDocQueryParam(name = "page", required = false)
    ])
    @UberDocQueryParam(name = "max", required = true)
    def list() { }

    @UberDocResource(object = Pod)
    @UberDocError(errorCode = "NF404", httpCode = 404)
    @UberDocHeader(name = "some header param")
    @UberDocUriParams([
            @UberDocUriParam(name = "firstId"),
            @UberDocUriParam(name = "secondId")
    ])
    @UberDocUriParam(name = "thirdId")
    def create() { }

    @UberDocHeader(name = "hdr")
    @UberDocResource(requestObject = Pod, responseObject = Pod)
    @UberDocUriParam(name = "id")
    @UberDocQueryParam(name = "foobar")
    def update() { }

    @UberDocResource()
    @UberDocUriParam(name = "id")
    def delete() { }

    @UberDocHeader(name = "never_reached")
    def foobar() {}
}
