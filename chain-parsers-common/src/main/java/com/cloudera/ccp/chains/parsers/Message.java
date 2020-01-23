package com.cloudera.ccp.chains.parsers;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A {@link Message} is consumed and parsed by a {@link Parser}.
 *
 * A {@link Message} is composed of a collection of fields. The message fields
 * are represented as ({@link FieldName}, {@link FieldValue}) pairs.
 *
 * A {@link Message} is immutable and a {@link MessageBuilder} should be used to
 * construct one.
 */
public class Message {

    /**
     * Constructs a {@link Message}.
     */
    public static class MessageBuilder {
        private Map<FieldName, FieldValue> fields;
        private Throwable error;

        public MessageBuilder() {
            this.fields = new HashMap<>();
        }

        public MessageBuilder withFields(Message message) {
            this.fields.putAll(message.fields);
            return this;
        }

        public MessageBuilder addField(FieldName name, FieldValue value) {
            this.fields.put(name, value);
            return this;
        }

        public MessageBuilder removeField(FieldName name) {
            this.fields.remove(name);
            return this;
        }

        public MessageBuilder withError(Throwable error) {
            this.error = error;
            return this;
        }

        public MessageBuilder withError(String message) {
            this.error = new IllegalStateException(message);
            return this;
        }

        public Message build() {
            return new Message(fields);
        }
    }

    private Map<FieldName, FieldValue> fields;
    private Throwable error;

    /**
     * Use {@link Message#builder} to build a message.
     */
    private Message(Map<FieldName, FieldValue> fields) {
        this(fields, null);
    }

    /**
     * Use {@link Message#builder} to build a message.
     */
    private Message(Map<FieldName, FieldValue> fields, Throwable error) {
        this.fields = new HashMap<>();
        this.fields.putAll(fields);
        this.error = error;
    }

    /**
     * @return A {@link MessageBuilder} that can be used to create a message.
     */
    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    public Optional<FieldValue> getField(FieldName fieldName) {
        if(fields.containsKey(fieldName)) {
            return Optional.of(fields.get(fieldName));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Throwable> getError() {
        return Optional.of(error);
    }

    @Override
    public String toString() {
        return "Message{" +
                "fields=" + fields +
                '}';
    }
}
