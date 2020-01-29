package com.cloudera.ccp.chains.parsers.core;

import com.cloudera.ccp.chains.parsers.ConfigName;
import com.cloudera.ccp.chains.parsers.ConfigValues;
import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Parser;

import java.util.Collections;
import java.util.List;

/**
 * A Parser that does nothing.
 */
public class NoopParser implements Parser {

    @Override
    public Message parse(Message message) {
        // do nothing
        return message;
    }

    @Override
    public List<FieldName> outputFields() {
        // do nothing
        return Collections.emptyList();
    }

    @Override
    public List<ConfigName> validConfigurations() {
        // do nothing
        return Collections.emptyList();
    }

    @Override
    public void configure(ConfigName configName, ConfigValues configValues) {
        // do nothing
    }
}
