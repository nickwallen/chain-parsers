package com.cloudera.ccp.parsers;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Message {
    private Map<FieldName, FieldValue> fields;

    public Message() {
        this.fields = new HashMap<>();
    }

    public Message(Map<FieldName, FieldValue> fields) {
        this();
        this.fields.putAll(fields);
    }

    public Optional<FieldValue> getField(FieldName fieldName) {
        if(fields.containsKey(fieldName)) {
            return Optional.of(fields.get(fieldName));
        } else {
            return Optional.empty();
        }

    }

    public Message withField(FieldName name, FieldValue value) {
        this.fields.put(name, value);
        return this;
    }

    public Message removeField(FieldName name) {
        this.fields.remove(name);
        return this;
    }

    @Override
    public String toString() {
        return "Message{" +
                "fields=" + fields +
                '}';
    }
}
