package com.cloudera.ccp.chains.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Parses delimited text like CSV.
 */
public class CSVParser implements Parser {

    /**
     * Defines which output fields are created.
     */
    public static class OutputField {
        private FieldName fieldName;
        private int index;

        public OutputField(FieldName fieldName, int index) {
            this.fieldName = fieldName;
            this.index = index;
        }

        public FieldName getFieldName() {
            return fieldName;
        }

        public void setFieldName(FieldName fieldName) {
            this.fieldName = fieldName;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    private FieldName inputField;
    private Regex delimiter;
    private List<OutputField> outputFields;

    public CSVParser() {
        outputFields = new ArrayList<>();
        delimiter = new Regex(",");
    }

    /**
     * @param inputField The name of the field containing the text to parse.
     */
    public CSVParser withInputField(FieldName inputField) {
        this.inputField = inputField;
        return this;
    }

    /**
     * @param delimiter A character or regular expression defining the delimiter used to split the text.
     */
    public CSVParser withDelimiter(Regex delimiter) {
        this.delimiter = delimiter;
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

    @Override
    public Message parse(Message input) {
        Message.MessageBuilder output = Message.builder()
                .withFields(input);

        Optional<FieldValue> fieldValue = input.getField(inputField);
        if(fieldValue.isPresent()) {
            String[] columns = fieldValue.get().toString().split(delimiter.toString());
            for(OutputField outputField : outputFields) {

                if(columns.length > outputField.index) {
                    FieldName newFieldName = outputField.fieldName;
                    FieldValue newFieldValue = FieldValue.of(columns[outputField.index]);
                    output.addField(newFieldName, newFieldValue);

                } else {
                    // TODO debug log; index does not exist in the data
                }
            }
        } else {
            // TODO debug log; input field missing from message
        }

        return output.build();
    }

    @Override
    public List<FieldName> outputFields() {
        return Collections.emptyList();
    }
}
