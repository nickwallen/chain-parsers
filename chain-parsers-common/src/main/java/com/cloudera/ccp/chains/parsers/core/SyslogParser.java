package com.cloudera.ccp.chains.parsers.core;

import com.cloudera.ccp.chains.parsers.ConfigName;
import com.cloudera.ccp.chains.parsers.ConfigValue;
import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.FieldValue;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.MessageParser;
import com.cloudera.ccp.chains.parsers.Parser;
import com.github.palindromicity.syslog.SyslogParserBuilder;
import com.github.palindromicity.syslog.SyslogSpecification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;
import static com.cloudera.ccp.chains.parsers.core.ParserUtils.requireN;

@MessageParser(name="Syslog", description="Parses Syslog according to RFC 3164 and 5424.")
public class SyslogParser implements Parser {
    public static final ConfigName inputFieldConfig = ConfigName.of("inputField", false);
    public static final ConfigName specConfig = ConfigName.of("specification", false);

    private FieldName inputField;
    private SyslogSpecification specification;

    public SyslogParser() {
        this.specification = SyslogSpecification.RFC_5424;
    }

    public SyslogParser withSpecification(SyslogSpecification specification) {
        this.specification = Objects.requireNonNull(specification);
        return this;
    }

    public SyslogParser withInputField(FieldName inputField) {
        this.inputField = Objects.requireNonNull(inputField);
        return this;
    }

    @Override
    public Message parse(Message input) {
        if(inputField == null) {
            throw new IllegalStateException("Input field has not been defined.");
        }
        Message.Builder output = Message.builder().withFields(input);
        Optional<FieldValue> value = input.getField(inputField);
        if(value.isPresent()) {
            new SyslogParserBuilder()
                    .forSpecification(specification)
                    .build()
                    .parseLine(value.get().toString())
                    .forEach((k, v) -> output.addField(FieldName.of(k), FieldValue.of(v.toString())));

        } else {
            output.withError(format("Message does not contain input field '%s'", inputField.toString()));
        }

        return output.build();
    }

    @Override
    public List<FieldName> outputFields() {
        // TODO implement me
        return Collections.emptyList();
    }

    @Override
    public List<ConfigName> validConfigurations() {
        return Arrays.asList(inputFieldConfig, specConfig);
    }

    @Override
    public void configure(ConfigName configName, List<ConfigValue> configValues) {
        if(inputFieldConfig.equals(configName)) {
            requireN(inputFieldConfig, configValues, 1);
            String inputFieldArg = configValues.get(0).getValue();
            withInputField(FieldName.of(inputFieldArg));

        } else if(specConfig.equals(configName)) {
            requireN(specConfig, configValues, 1);
            String specArg = configValues.get(0).getValue();
            withSpecification(SyslogSpecification.valueOf(specArg));

        } else {
            throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", configName));
        }
    }
}
