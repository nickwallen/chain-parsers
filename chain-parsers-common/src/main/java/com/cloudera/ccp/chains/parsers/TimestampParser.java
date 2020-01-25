package com.cloudera.ccp.chains.parsers;

import java.util.Arrays;
import java.util.List;

/**
 * A simple parser that adds the current system time as
 * a field to the message.
 */
public class TimestampParser implements Parser {
    private FieldName timestampFieldName;

    public TimestampParser() {
        this.timestampFieldName = FieldName.of("timestamp");
    }

    @Override
    public Message parse(Message input) {
        Long now = System.currentTimeMillis();
        FieldValue timestamp = FieldValue.of(Long.toString(now));
        return Message.builder()
                .withFields(input)
                .addField(timestampFieldName, timestamp)
                .build();
    }

    @Override
    public List<FieldName> outputFields() {
        return Arrays.asList(timestampFieldName);
    }

    /**
     * @param fieldName The name of the field added to each message.
     */
    public TimestampParser withFieldName(FieldName fieldName) {
        this.timestampFieldName = fieldName;
        return this;
    }
}
