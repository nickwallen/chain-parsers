package com.cloudera.ccp.chains.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Parses delimited text like CSV.
 */
public class CSVParser implements Parser {

    /**
     * Defines an output field that is created by the parser.
     */
    private static class OutputField {
        private FieldName fieldName;
        private int index;

        public OutputField(FieldName fieldName, int index) {
            this.fieldName = Objects.requireNonNull(fieldName);
            this.index = index;
        }
    }

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

    /**
     * @param delimiter A character or regular expression defining the delimiter used to split the text.
     */
    public CSVParser withDelimiter(Regex delimiter) {
        this.delimiter = Objects.requireNonNull(delimiter);
        return this;
    }

    /**
     * @param fieldName The name of a field to create.
     * @param index The 0-based index defining which delimited element is added to the field.
     */
    public CSVParser withOutputField(FieldName fieldName, int index) {
        outputFields.add(new OutputField(fieldName, index));
        return this;
    }

    /**
     * @param trimWhitespace True, if whitespace should be trimmed from each value. Otherwise, false.
     */
    public CSVParser trimWhitespace(boolean trimWhitespace) {
        this.trimWhitespace = trimWhitespace;
        return this;
    }

    @Override
    public Message parse(Message input) {
        Message.Builder output = Message.builder()
                .withFields(input);

        Optional<FieldValue> fieldValue = input.getField(inputField);
        if(fieldValue.isPresent()) {
            doParse(fieldValue.get().toString(), output);
        } else {
            output.withError(format("Missing the input field '%s'", inputField.toString()));
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
        return Collections.emptyList();
    }
}
