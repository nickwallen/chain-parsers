package com.cloudera.ccp.chains.parsers;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Message} is consumed and parsed by a {@link Parser}.
 *
 * A {@link Message} is composed of a collection of fields. The message fields
 * are represented as ({@link FieldName}, {@link FieldValue}) pairs.
 *
 * A {@link Message} is immutable and a {@link Builder} should be used to
 * construct one.
 */
public class Message {

    /**
     * Constructs a {@link Message}.
     */
    public static class Builder {
        private Map<FieldName, FieldValue> fields;
        private Throwable error;

        public Builder() {
            this.fields = new HashMap<>();
        }

        public Builder withFields(Message message) {
            Objects.requireNonNull(message);
            this.fields.putAll(message.fields);
            return this;
        }

        public Builder addField(FieldName name, FieldValue value) {
            this.fields.put(Objects.requireNonNull(name), Objects.requireNonNull(value));
            return this;
        }

        public Builder removeField(FieldName name) {
            this.fields.remove(Objects.requireNonNull(name));
            return this;
        }

        public Builder withError(Throwable error) {
            this.error = Objects.requireNonNull(error);
            return this;
        }

        public Builder withError(String message) {
            this.error = new IllegalStateException(Objects.requireNonNull(message));
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }

    private Map<FieldName, FieldValue> fields;
    private Throwable error;

    private Message(Builder builder) {
        this.fields = new HashMap<>();
        this.fields.putAll(builder.fields);
        this.error = builder.error;
    }

    /**
     * @return A {@link Builder} that can be used to create a message.
     */
    public static Builder builder() {
        return new Builder();
    }

    public Optional<FieldValue> getField(FieldName fieldName) {
        if(fields.containsKey(fieldName)) {
            return Optional.of(fields.get(fieldName));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Throwable> getError() {
        return Optional.ofNullable(error);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return new EqualsBuilder()
                .append(fields, message.fields)
                .append(error, message.error)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fields)
                .append(error)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Message{" +
                "fields=" + fields +
                ", error=" + error +

                '}';
    }
}
