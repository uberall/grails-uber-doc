package uberdoc.messages

class MessageFallback {

    MessageReader messageReader

    MessageFallback(MessageReader mr) {
        this.messageReader = mr
    }

    String fallbackToMessageSourceIfAnnotationDoesNotOverride(String messageKey, String annotatedValue){
        messageKey = messageKey.replace(" ", ".")
        def valueFromMessageSource = messageReader.get(messageKey)

        if(annotatedValue && (!valueFromMessageSource || valueFromMessageSource == messageKey || valueFromMessageSource.contains(messageKey))){
            return annotatedValue
        }
        return valueFromMessageSource
    }


}
