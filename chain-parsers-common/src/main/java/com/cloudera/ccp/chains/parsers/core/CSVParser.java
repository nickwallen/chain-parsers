package com.cloudera.ccp.chains.parsers.core;

import com.cloudera.ccp.chains.parsers.ConfigName;
import com.cloudera.ccp.chains.parsers.ConfigValue;
import com.cloudera.ccp.chains.parsers.ConfigValues;
import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.FieldValue;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Parser;
import com.cloudera.ccp.chains.parsers.Regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Parses delimited text like CSV.
 */
public class CSVParser implements Parser {

    /**
     * Defines an output field that is created by the parser.
     */
    static class OutputField {
        FieldName fieldName;
        int index;

        OutputField(FieldName fieldName, int index) {
            this.fieldName = Objects.requireNonNull(fieldName);
            this.index = index;
        }
    }

    public static final ConfigName inputConfig = ConfigName.of("input", true);
    public static final ConfigName outputConfig = ConfigName.of("output", true);
    public static final ConfigName delimiterConfig = ConfigName.of("delimiter", false);
    public static final ConfigName trimConfig = ConfigName.of("trim", false);

    private FieldName inputField;
    private Regex delimiter;
    private List<OutputField> outputFields;
    private boolean trimWhitespace;

    public CSVParser() {
        outputFields = new ArrayList<>();
        delimiter = Regex.of(",");
        trimWhitespace = true;
    }

    /**
     * @param inputField The name of the field containing the text to parse.
     */
    public CSVParser withInputField(FieldName inputField) {
        this.inputField = Objects.requireNonNull(inputField);
        return this;
    }

    public FieldName getInputField() {
        return inputField;
    }

    /**
     * @param delimiter A character or regular expression defining the delimiter used to split the text.
     */
    public CSVParser withDelimiter(Regex delimiter) {
        this.delimiter = Objects.requireNonNull(delimiter);
        return this;
    }

    public Regex getDelimiter() {
        return delimiter;
    }

    /**
     * @param fieldName The name of a field to create.
     * @param index The 0-based index defining which delimited element is added to the field.
     */
    public CSVParser withOutputField(FieldName fieldName, int index) {
        outputFields.add(new OutputField(fieldName, index));
        return this;
    }

    public List<OutputField> getOutputFields() {
        return outputFields;
    }

    /**
     * @param trimWhitespace True, if whitespace should be trimmed from each value. Otherwise, false.
     */
    public CSVParser trimWhitespace(boolean trimWhitespace) {
        this.trimWhitespace = trimWhitespace;
        return this;
    }

    public boolean isTrimWhitespace() {
        return trimWhitespace;
    }

    @Override
    public List<ConfigName> validConfigurations() {
        return Arrays.asList(inputConfig, outputConfig, delimiterConfig, trimConfig);
    }

    @Override
    public Message parse(Message input) {
        Message.Builder output = Message.builder()
                .withFields(input);

        Optional<FieldValue> fieldValue = input.getField(inputField);
        if(fieldValue.isPresent()) {
            doParse(fieldValue.get().toString(), output);
        } else {
            output.withError(format("Input field has not been defined"));
        }

        return output.build();
    }

    private void doParse(String valueToParse, Message.Builder output) {
        String[] columns = valueToParse.split(delimiter.toString());
        int width = columns.length;
        for(OutputField outputField : outputFields) {
            if(width > outputField.index) {
                // create a new output field
                String column = columns[outputField.index];
                if(trimWhitespace) {
                    column = column.trim();
                }
                output.addField(outputField.fieldName, FieldValue.of(column));

            } else {
                String err = format("Found %d column(s), index %d does not exist.", width, outputField.index);
                output.withError(err);
            }
        }
    }

    @Override
    public List<FieldName> outputFields() {
        return outputFields
                .stream()
                .map(outputField -> outputField.fieldName)
                .collect(Collectors.toList());
    }

    @Override
    public void configure(ConfigName configName, ConfigValues configValues) {
        if(inputConfig.equals(configName)) {
            configureInput(configValues);

        } else if(outputConfig.equals(configName)) {
            configureOutput(configValues);

        } else if(delimiterConfig.equals(configName)) {
            configureDelimiter(configValues);

        } else if(trimConfig.equals(configName)) {
            configureTrim(configValues);
        }
    }

    private void configureTrim(ConfigValues configValues) {
        // set the whitespace trim
        requireN(configValues, trimConfig.getName(), 1);
        ConfigValue value1 = configValues.values().get(0);
        trimWhitespace(Boolean.valueOf(value1.getValue()));
    }

    private void configureDelimiter(ConfigValues configValues) {
        // set the delimiter
        requireN(configValues, delimiterConfig.getName(), 1);
        ConfigValue value1 = configValues.values().get(0);
        withDelimiter(Regex.of(value1.getValue()));
    }

    private void configureInput(ConfigValues configValues) {
        requireN(configValues, inputConfig.getName(), 1);
        ConfigValue value1 = configValues.values().get(0);
        withInputField(FieldName.of(value1.getValue()));
    }

    private void configureOutput(ConfigValues configValues) {
        requireN(configValues, outputConfig.getName(), 2);

        int index = -1;
        FieldName outputField = null;
        for(ConfigValue value: configValues.values()) {
            if("label".equals(value.getKey())) {
                outputField = FieldName.of(value.getValue());
            } else if("index".equals(value.getKey())) {
                index = Integer.parseInt(value.getValue());
            } else {
                throw new IllegalArgumentException("TODO");
            }
        }

        if(outputField != null && index != -1) {
            withOutputField(outputField, index);
        } else {
            throw new IllegalArgumentException("TODO");
        }
    }

    private void requireN(ConfigValues configValues, String name, int count) {
        if(configValues.size() != count) {
            String msg = "For '%s' expected %d value(s), but got %d; ";
            throw new IllegalArgumentException(String.format(msg, name, count, configValues.size()));
        }
    }
}
