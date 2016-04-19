package uberdoc.messages

import spock.lang.Specification

class MessageFallbackSpec extends Specification {

    MessageFallback fallback
    MessageReader messageReaderMock

    void setup(){
        messageReaderMock = Mock()
        fallback = new MessageFallback(messageReaderMock)
    }

    void "if annotatedValue is null, fallbackToMessageSourceIfAnnotationDoesNotOverride returns what comes from messageSource"(){
        given:
        String messageKey = "message.key"
        String annotatedValue

        when:
        String msg = fallback.fallbackToMessageSourceIfAnnotationDoesNotOverride(messageKey, annotatedValue)

        then:
        msg == "message.key"
        1 * messageReaderMock.get('message.key') >> "message.key"
    }

    void "if annotatedValue is null, fallbackToMessageSourceIfAnnotationDoesNotOverride replaces spaces and returns what comes from messageSource"(){
        given:
        String messageKey = "message key"
        String annotatedValue

        when:
        String msg = fallback.fallbackToMessageSourceIfAnnotationDoesNotOverride(messageKey, annotatedValue)

        then:
        msg == "message.key"
        1 * messageReaderMock.get('message.key') >> "message.key"
    }

    void "if annotatedValue is not null, and messageSource returns something that is different from message key, fallbackToMessageSourceIfAnnotationDoesNotOverride returns what comes from messageSource"(){
        given:
        String messageKey = "message.key"
        String annotatedValue = "custom value"

        when:
        String msg = fallback.fallbackToMessageSourceIfAnnotationDoesNotOverride(messageKey, annotatedValue)

        then:
        msg == "value from message source"
        1 * messageReaderMock.get('message.key') >> "value from message source"
    }

    void "if annotatedValue is not null, and messageSource returns the very message key, fallbackToMessageSourceIfAnnotationDoesNotOverride returns what is used in annotation"(){
        given:
        String messageKey = "message.key"
        String annotatedValue = "custom value"

        when:
        String msg = fallback.fallbackToMessageSourceIfAnnotationDoesNotOverride(messageKey, annotatedValue)

        then:
        msg == "custom value"
        1 * messageReaderMock.get('message.key') >> "message.key"
    }

    void "if annotatedValue is not null, and messageSource returns a link to the message key, fallbackToMessageSourceIfAnnotationDoesNotOverride returns what is used in annotation"(){
        given:
        String messageKey = "message.key"
        String annotatedValue = "custom value"

        when:
        String msg = fallback.fallbackToMessageSourceIfAnnotationDoesNotOverride(messageKey, annotatedValue)

        then:
        msg == "custom value"
        1 * messageReaderMock.get('message.key') >> "<a href=http://localhost:8080/admin/messages/?search=message.key</a>"
    }

    void "if annotatedValue is not null, and messageSource returns null, fallbackToMessageSourceIfAnnotationDoesNotOverride returns what is used in annotation"(){
        given:
        String messageKey = "message.key"
        String annotatedValue = "custom value"

        when:
        String msg = fallback.fallbackToMessageSourceIfAnnotationDoesNotOverride(messageKey, annotatedValue)

        then:
        msg == "custom value"
        1 * messageReaderMock.get('message.key') >> null
    }

    void "the only way fallbackToMessageSourceIfAnnotationDoesNotOverride returns null is if messageSource returns null and annotatedValue is null"(){
        given:
        String messageKey = "message.key"
        String annotatedValue

        when:
        String msg = fallback.fallbackToMessageSourceIfAnnotationDoesNotOverride(messageKey, annotatedValue)

        then:
        msg == null
        1 * messageReaderMock.get('message.key') >> null
    }
}
