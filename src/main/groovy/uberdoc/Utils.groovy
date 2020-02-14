package uberdoc

class Utils {

    def grailsApplication

    Utils(grailsApplication) {
        this.grailsApplication = grailsApplication
    }

    boolean shouldPublish(a) {
        return (!a.internalOnly() || grailsApplication.config.uberdoc?.publishInternalOnly)
    }
}
