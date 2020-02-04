package com.cloudera.ccp.chains.parsers.core;

import com.cloudera.ccp.chains.parsers.ConfigName;
import com.cloudera.ccp.chains.parsers.ConfigValue;
import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.MessageParser;
import com.cloudera.ccp.chains.parsers.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A parser which can remove fields from a message.
 */
@MessageParser(name="Remove Field(s)", description="Removes a message field.")
public class RemoveFieldParser implements Parser {
    static final ConfigName removeConfig = ConfigName.of("remove", true);
    private List<FieldName> fieldsToRemove;

    public RemoveFieldParser() {
        fieldsToRemove = new ArrayList<>();
    }

    public RemoveFieldParser removeField(FieldName fieldName) {
        fieldsToRemove.add(fieldName);
        return this;
    }

    @Override
    public Message parse(Message message) {
        return Message.builder()
                .withFields(message)
                .removeFields(fieldsToRemove)
                .build();
    }

    @Override
    public List<FieldName> outputFields() {
        return Collections.emptyList();
    }

    @Override
    public List<ConfigName> validConfigurations() {
        return Arrays.asList(removeConfig);
    }

    @Override
    public void configure(ConfigName configName, List<ConfigValue> configValues) {
        if(removeConfig.equals(configName)) {
            for(ConfigValue configValue: configValues) {
                removeField(FieldName.of(configValue.getValue()));
            }
        } else {
            throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", configName));
        }
    }

    List<FieldName> getFieldsToRemove() {
        return fieldsToRemove;
    }
}
