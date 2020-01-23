package com.cloudera.ccp.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CSVParser implements Parser {
    public static class Label {
        private FieldName fieldName;
        private int index;

        public Label(FieldName fieldName, int index) {
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
    private String delimiter;
    private List<Label> labels;

    public CSVParser() {
        labels = new ArrayList<>();
        delimiter = ",";
    }

    public CSVParser withInputField(FieldName inputField) {
        this.inputField = inputField;
        return this;
    }

    public CSVParser withDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public CSVParser withLabel(FieldName fieldName, int index) {
        labels.add(new Label(fieldName, index));
        return this;
    }

    @Override
    public ParserResult parse(Message message) {
        Optional<FieldValue> fieldValue = message.getField(inputField);
        if(fieldValue.isPresent()) {
            String[] columns = fieldValue.get().getString().split(delimiter);
            for(Label label: labels) {

                if(columns.length > label.index) {
                    FieldName newFieldName = label.fieldName;
                    FieldValue newFieldValue = new FieldValue(columns[label.index]);
                    message.withField(newFieldName, newFieldValue);

                } else {
                    // TODO debug log; index does not exist in the data
                }
            }
        } else {
            // TODO debug log; input field missing from message
        }

        return ParserResult.success(message, this);
    }

    @Override
    public List<FieldName> outputFields() {
        return Collections.emptyList();
    }
}
