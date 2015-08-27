package uberdoc.messages

import org.springframework.context.NoSuchMessageException

class MessageReader {

    def messageSource
    Locale locale

    MessageReader(messageSource, Locale locale) {
        this.messageSource = messageSource
        this.locale = locale
    }

    String get(String key){
        String ret = null

        try{
            ret = messageSource.getMessage(key, new Object[0], locale)
        }
        catch (NoSuchMessageException e){
            // do nothing
        }

        return ret
    }
}
