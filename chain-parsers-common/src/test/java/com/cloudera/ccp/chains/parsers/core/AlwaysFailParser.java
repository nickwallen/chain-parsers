package com.cloudera.ccp.chains.parsers.core;

import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Parser;

import java.util.Collections;
import java.util.List;

/**
 * A {@link Parser} that always fails which is useful for testing.
 */
public class AlwaysFailParser implements Parser {
    @Override
    public Message parse(Message message) {
        return Message.builder()
                .withFields(message)
                .withError("always fails")
                .build();
    }

    @Override
    public List<FieldName> outputFields() {
        return Collections.emptyList();
    }
}
