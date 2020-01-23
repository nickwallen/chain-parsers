package com.cloudera.ccp.parsers;

import java.util.Arrays;
import java.util.List;

/**
 * A simple parser that adds the current system time as
 * a field to the message.
 */
public class TimestampParser implements Parser {
    private FieldName timestampFieldName;

    public TimestampParser() {
        this.timestampFieldName = new FieldName("timestamp");
    }

    @Override
    public ParserResult parse(Message message) {
        long timestamp = System.currentTimeMillis();

        // TODO make message immutable?
        message.withField(timestampFieldName, new FieldValue(Long.toString(timestamp)));
        return ParserResult.success(message, this);
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
