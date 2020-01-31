package com.cloudera.ccp.chains.parsers.core;

import com.cloudera.ccp.chains.parsers.ConfigName;
import com.cloudera.ccp.chains.parsers.ConfigValue;
import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.MessageParser;
import com.cloudera.ccp.chains.parsers.Parser;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A {@link Parser} that always fails.
 *
 * <p>This can be used with a {@link com.cloudera.ccp.chains.links.RouterLink}
 * to flag when unexpected conditions are encountered in the data.
 */
@MessageParser(name="Always Fails", description = "A parser that always fails to indicate an error condition.")
public class AlwaysFailParser implements Parser {
    private Throwable error;

    public AlwaysFailParser() {
        error = new IllegalStateException("Parsing error encountered");
    }

    @Override
    public Message parse(Message message) {
        return Message.builder()
                .withFields(message)
                .withError(error)
                .build();
    }

    public AlwaysFailParser withError(String message) {
        Objects.requireNonNull(message);
        error = new IllegalStateException(message);
        return this;
    }

    public AlwaysFailParser withError(Throwable error) {
        this.error = Objects.requireNonNull(error);
        return this;
    }

    @Override
    public List<FieldName> outputFields() {
        return Collections.emptyList();
    }

    @Override
    public List<ConfigName> validConfigurations() {
        return Collections.emptyList();
    }

    @Override
    public void configure(ConfigName configName, List<ConfigValue> configValues) {
        // nothing to do
    }
}
