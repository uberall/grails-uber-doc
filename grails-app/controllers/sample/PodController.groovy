package sample

import uberdoc.annotation.UberDocController
import uberdoc.annotation.UberDocError
import uberdoc.annotation.UberDocErrors
import uberdoc.annotation.UberDocHeader
import uberdoc.annotation.UberDocHeaders
import uberdoc.annotation.UberDocQueryParam
import uberdoc.annotation.UberDocQueryParams
import uberdoc.annotation.UberDocResource
import uberdoc.annotation.UberDocUriParam
import uberdoc.annotation.UberDocUriParams

@UberDocErrors([
        @UberDocError(errorCode = "XYZ123", httpCode = 412, description = "this is a general 412 error, to be applied to all actions/resources in this file"),
        @UberDocError(errorCode = "ABC456", httpCode = 400, description = "this is a general 409 error, to be applied to all actions/resources in this file"),
        @UberDocError(errorCode = "CONF409", httpCode = 409, description = "all actions in this file may throw a 409 to indicate conflict")
])
@UberDocHeaders([
        @UberDocHeader(name = "publicToken", sampleValue = "all methods in this file should send a header param", description = "this param should be sent within the headers"),
        @UberDocHeader(name = "other token", sampleValue = "just some other token that every method should send", description = "this param should also be sent within the headers")
])
@UberDocController
class PodController { // this example simulates a CRUD-ish controller

    @UberDocResource(requestObject = Pod, responseCollectionOf = Pod, description = "this resource allows all Pods to be retrieved from DB")
    @UberDocError(errorCode = "NF404", httpCode = 404, description = "returned if the resource does not exist")
    @UberDocUriParam(name = "id", description = "the id of the Pod to be retrieved from DB", sampleValue = "4")
    @UberDocQueryParam(name = "id")
    def get() { }

    @UberDocHeader(name = "hdr", sampleValue = "sample")
    @UberDocResource(responseObject = Pod, description = "this resource allows all Pods to be retrieved from DB")
    @UberDocQueryParams([
            @UberDocQueryParam(name = "page", description = "pagination", required = false, sampleValue = "1", isCollection = false)
    ])
    @UberDocQueryParam(name = "max", description = "max desc", required = true, sampleValue = "100", isCollection = true)
    def list() { }

    @UberDocResource(object = Pod)
    @UberDocError(errorCode = "NF404", httpCode = 404)
    @UberDocHeader(name = "some header param", sampleValue = "hdr", description = "this is just something else to be sent on creation")
    @UberDocUriParams([
            @UberDocUriParam(name = "firstId", description = "the first id", sampleValue = "1"),
            @UberDocUriParam(name = "secondId", description = "the second id", sampleValue = "2")
    ])
    @UberDocUriParam(name = "thirdId", description = "the third id", sampleValue = "3")
    def create() { }

    @UberDocHeader(name = "hdr")
    @UberDocResource(object = Pod)
    @UberDocQueryParam(name = "id", description = "the id of the Pod to be retrieved from DB", sampleValue = "4")
    @UberDocUriParam(name = "id")
    def update() { }

    @UberDocQueryParam(name = "id", description = "the id of the Pod to be retrieved from DB", sampleValue = "4")
    def delete() { }
}
