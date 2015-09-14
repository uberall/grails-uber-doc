class UrlMappings {

    static mappings = {
//        "/$controller/$action?/$id?(.$format)?"{
//            constraints {
//                // apply constraints here
//            }
//        }

        "/"(view: "/index")
        "500"(view: '/error')

        "/api/pods"(controller: 'pod', action: [POST: "create", GET: "list"], parseRequest: true)
        "/api/pods/$id/create"(controller: 'pod', action: [POST: "create", deprecated: true], parseRequest: true)
        "/api/pods/$id"(controller: 'pod', action: [PUT: "update", PATCH: "update", GET: "get", DELETE: "delete"], parseRequest: true)

        "/api/others"(controller: 'other', action: [POST: "create"], parseRequest: true)
        "/api/others/$id"(controller: 'other', action: [PUT: "update", PATCH: "update", GET: "get", DELETE: "delete"], parseRequest: true)

        "/api/something/else"(controller: 'apiSomethingElse', action: [POST: "create"], parseRequest: true)
    }
}
