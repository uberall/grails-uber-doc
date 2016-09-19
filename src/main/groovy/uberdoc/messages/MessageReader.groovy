package uberdoc.messages

import org.springframework.context.NoSuchMessageException

class MessageReader {

    private static final Object[] NO_ARGS = new Object[0]

    def messageSource
    Locale locale

    MessageReader(messageSource, Locale locale) {
        this.messageSource = messageSource
        this.locale = locale
    }

    String get(String key) {
        try {
            return messageSource.getMessage(key, NO_ARGS, locale)
        }
        catch (NoSuchMessageException e) {
            return key // in case we have no message, return the key to avoid exceptions and make it easier to find
        }
    }
}
