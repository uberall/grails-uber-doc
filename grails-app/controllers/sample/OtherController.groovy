package sample

import uberdoc.annotation.*

@UberDocHeaders([
        @UberDocHeader(name = "publicToken"),
        @UberDocHeader(name = "other token")
])
class OtherController {

    @UberDocResource(requestObject = Pod, responseObject = Pod)
    @UberDocError(errorCode = "NF404", httpCode = 404)
    @UberDocUriParam(name = "id")
    def get() { }

    @UberDocHeader(name = "hdr")
    @UberDocResource(responseObject = Pod)
    @UberDocQueryParams([
            @UberDocQueryParam(name = "page", required = false)
    ])
    @UberDocQueryParam(name = "max", required = true)
    def list() { }

    @UberDocResource(object = Pod)
    @UberDocHeader(name = "some header param")
    @UberDocUriParams([
            @UberDocUriParam(name = "firstId"),
            @UberDocUriParam(name = "secondId")
    ])
    @UberDocUriParam(name = "thirdId")
    def create() { }

    @UberDocResource(object = Pod)
    @UberDocQueryParam(name = "id")
    def update() { }

    @UberDocQueryParam(name = "id")
    def delete() { }
}
