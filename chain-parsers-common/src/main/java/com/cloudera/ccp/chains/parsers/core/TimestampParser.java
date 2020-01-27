package com.cloudera.ccp.chains.parsers.core;

import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.FieldValue;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Parser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A parser that adds the current system time as a field to the message. Useful for
 * tracking the time when a message was parsed.
 */
public class TimestampParser implements Parser {

    public static class Clock {
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }

    private FieldName timestampFieldName;
    private Clock clock;

    public TimestampParser() {
        this.timestampFieldName = FieldName.of("timestamp");
        this.clock = new Clock();
    }

    @Override
    public Message parse(Message input) {
        Long now = clock.currentTimeMillis();
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
        this.timestampFieldName = Objects.requireNonNull(fieldName);
        return this;
    }

    /**
     * @param clock A {@link Clock} to use during testing.
     */
    public TimestampParser withClock(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
        return this;
    }

    public FieldName getTimestampFieldName() {
        return timestampFieldName;
    }
}
