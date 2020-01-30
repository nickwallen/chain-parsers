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
@MessageParser(
        name="Remove Field",
        description="Removes a field from a message."
)
public class RemoveFieldParser implements Parser {
    private static ConfigName toRemoveConfig = ConfigName.of("toRemove", true);

    private List<FieldName> toRemove;

    public RemoveFieldParser() {
        toRemove = new ArrayList<>();
    }

    public RemoveFieldParser removeField(FieldName fieldName) {
        toRemove.add(fieldName);
        return this;
    }

    @Override
    public Message parse(Message message) {
        return Message.builder()
                .withFields(message)
                .removeFields(toRemove)
                .build();
    }

    @Override
    public List<FieldName> outputFields() {
        return Collections.emptyList();
    }

    @Override
    public List<ConfigName> validConfigurations() {
        return Arrays.asList(toRemoveConfig);
    }

    @Override
    public void configure(ConfigName configName, List<ConfigValue> configValues) {
        if(toRemoveConfig.equals(configName)) {
            for(ConfigValue configValue: configValues) {
                removeField(FieldName.of(configValue.getValue()));
            }
        }
    }
}
