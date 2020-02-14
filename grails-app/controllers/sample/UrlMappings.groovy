package sample

class UrlMappings {

    static mappings = {
        group("/api") {

            group("/pods") {
                "/"(controller: 'pod', action: 'list', method: 'GET')
                "/"(controller: 'pod', action: 'create', method: 'POST')
                "/internal"(controller: 'pod', action: 'internalMethod', method: 'GET')
                "/$id"(controller: 'pod', action: 'get', method: 'GET')
                "/$id"(controller: 'pod', action: 'update', method: 'PATCH')
                "/$id"(controller: 'pod', action: 'update', method: 'PUT')
                "/$id"(controller: 'pod', action: 'delete', method: 'DELETE')
            }

            group("/internal") {
                "/"(controller: 'internal', action: 'list', method: 'GET')
                "/$id"(controller: 'internal', action: 'get', method: 'GET')
            }
        }

        "/api/foobar"(controller: 'pod', action: [GET: 'foobar'], parseRequest: true)

        "/api/others"(controller: 'other', action: [POST: "create"], parseRequest: true)
        "/api/others/$id"(controller: 'other', action: [PUT: "update", PATCH: "update", GET: "get", DELETE: "delete"], parseRequest: true)

        "/api/something/else"(controller: 'apiSomethingElse', action: [POST: "create"], parseRequest: true)
    }
}
