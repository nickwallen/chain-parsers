package com.cloudera.ccp.chains.parsers;

import java.util.Collections;
import java.util.List;

/**
 * A Parser that does nothing.
 */
public class NoopParser implements Parser {

    @Override
    public Message parse(Message message) {
        return Message.builder()
                .withFields(message)
                .build();
    }

    @Override
    public List<FieldName> outputFields() {
        return Collections.emptyList();
    }
}
