package uberdoc

class ApiDocumentation {
    Map objects
    List<Map> resources
    List<Map.Entry> resourcesByObjects

    ApiDocumentation(Map objects, List resources) {
        this.objects = objects
        this.resources = resources
        sort()
    }

    private void sort() {
        sortResourcesProperties()
        sortObjectsProperties()

        if (resources) {
            resources = resources.sort { "${it.uri} ${getOrderNumber(it.method as String)}" }
            resourcesByObjects = resources.groupBy { resource ->
                return resource.uri.split("/")[2]
            }.entrySet().toList() as List<Map.Entry>
        }
        if (objects) {
            objects = objects.sort { it.value.name }
        }
    }

    private void sortObjectsProperties() {
        if (!objects) {
            return
        }

        objects.each { Map.Entry object ->
            object.value.properties.sort { it.name }
        }
    }

    private void sortResourcesProperties() {
        if (!resources) {
            return
        }

        resources.each { Map resource ->
            resource.queryParams.sort { it.name }
            resource.uriParams.sort { it.name }
            resource.bodyParams.sort { it.name }
        }
    }

    /**
     * Simple helper method to give all possible API methods (GET, PATCH, PUT, POST, DELETE) a specific order (not an alphabetically one)
     * Each method will be represented by an int value ranging from 0 to 4
     * For everything else besides the listed methods 99 will be returned
     * @param method
     * @return
     */
    private static int getOrderNumber(String method) {
        switch (method) {
            case 'GET':
                return 0
            case 'POST':
                return 1
            case 'PUT':
                return 2
            case 'PATCH':
                return 3
            case 'DELETE':
                return 4
            default:
                return 99
        }
    }
}
