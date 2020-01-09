package uberdoc

import java.lang.annotation.Annotation

class Utils {

    def grailsApplication

    Utils(grailsApplication) {
        this.grailsApplication = grailsApplication
    }

    boolean shouldPublish(Annotation a) {
        if (!a) {
            return false
        }

        if (!a.h.memberValues?.internalOnly) {
            return true
        }

        if (grailsApplication.config.uberdoc?.publishInternalOnly) {
            return true
        }

        return false
    }
}
