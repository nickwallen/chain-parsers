package com.cloudera.ccp.chains.parsers.core;

import com.cloudera.ccp.chains.parsers.ConfigValue;
import com.cloudera.ccp.chains.parsers.ConfigValues;
import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.FieldValue;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Regex;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CSVParserTest {

    @Test
    void parseCSV() {
        FieldValue csvToParse = FieldValue.of("value1, value2, value3, value4");
        Message input = Message.builder()
                .addField(FieldName.of("input"), csvToParse)
                .build();

        // parse the message
        Message output = new CSVParser()
                .withDelimiter(Regex.of(","))
                .withInputField(FieldName.of("input"))
                .withOutputField(FieldName.of("first"), 0)
                .withOutputField(FieldName.of("second"), 1)
                .withOutputField(FieldName.of("third"), 2)
                .parse(input);

        // input field should still exist
        assertEquals(csvToParse, output.getField(FieldName.of("input")).get());

        // new output fields
        assertEquals(FieldValue.of("value1"), output.getField(FieldName.of("first")).get());
        assertEquals(FieldValue.of("value2"), output.getField(FieldName.of("second")).get());
        assertEquals(FieldValue.of("value3"), output.getField(FieldName.of("third")).get());

        // no errors
        assertFalse(output.getError().isPresent());
    }

    @Test
    void parseTSV() {
        FieldValue tsvToParse = FieldValue.of("value1\t value2\t value3\t value4");
        Message input = Message.builder()
                .addField(FieldName.of("input"), tsvToParse)
                .build();

        // parse the message
        Message output = new CSVParser()
                .withDelimiter(Regex.of("\t"))
                .withInputField(FieldName.of("input"))
                .withOutputField(FieldName.of("first"), 0)
                .withOutputField(FieldName.of("second"), 1)
                .withOutputField(FieldName.of("third"), 2)
                .parse(input);

        // input field should still exist
        assertEquals(tsvToParse, output.getField(FieldName.of("input")).get());

        // new output fields
        assertEquals(FieldValue.of("value1"), output.getField(FieldName.of("first")).get());
        assertEquals(FieldValue.of("value2"), output.getField(FieldName.of("second")).get());
        assertEquals(FieldValue.of("value3"), output.getField(FieldName.of("third")).get());

        // no errors
        assertFalse(output.getError().isPresent());
    }

    @Test
    void missingInputField() {
        Message input = Message.builder()
                .build();

        // parse the message
        Message output = new CSVParser()
                .withDelimiter(Regex.of(","))
                .withInputField(FieldName.of("input"))
                .withOutputField(FieldName.of("first"), 0)
                .parse(input);

        // expect an error
        assertTrue(output.getError().isPresent());
    }

    @Test
    void missingOutputField() {
        FieldValue csvToParse = FieldValue.of("value1, value2, value3, value4");
        Message input = Message.builder()
                .addField(FieldName.of("input"), csvToParse)
                .build();

        // there are only 4 fields in the CSV, but trying to use an index of 10
        int index = 10;
        Message output = new CSVParser()
                .withDelimiter(Regex.of(","))
                .withInputField(FieldName.of("input"))
                .withOutputField(FieldName.of("first"), index)
                .parse(input);

        // input field should still exist
        assertEquals(csvToParse, output.getField(FieldName.of("input")).get());

        // expect an error
        assertTrue(output.getError().isPresent());
    }

    @Test
    void emptyInputField() {
        // the input field is empty
        FieldValue csvToParse = FieldValue.of("");
        Message input = Message.builder()
                .addField(FieldName.of("input"), csvToParse)
                .build();

        // parse the message
        Message output = new CSVParser()
                .withDelimiter(Regex.of(","))
                .withInputField(FieldName.of("input"))
                .withOutputField(FieldName.of("first"), 0)
                .withOutputField(FieldName.of("second"), 1)
                .withOutputField(FieldName.of("third"), 2)
                .parse(input);

        // input field should still exist
        assertEquals(csvToParse, output.getField(FieldName.of("input")).get());

        // expect an error
        assertTrue(output.getError().isPresent());
    }

    @Test
    void noWhitespaceTrim() {
        FieldValue csvToParse = FieldValue.of(" value1, value2, value3, value4");
        Message input = Message.builder()
                .addField(FieldName.of("input"), csvToParse)
                .build();

        // parse the message
        Message output = new CSVParser()
                .trimWhitespace(false)
                .withDelimiter(Regex.of(","))
                .withInputField(FieldName.of("input"))
                .withOutputField(FieldName.of("first"), 0)
                .withOutputField(FieldName.of("second"), 1)
                .withOutputField(FieldName.of("third"), 2)
                .parse(input);

        // input field should still exist
        assertEquals(csvToParse, output.getField(FieldName.of("input")).get());

        // new output fields
        assertEquals(FieldValue.of(" value1"), output.getField(FieldName.of("first")).get());
        assertEquals(FieldValue.of(" value2"), output.getField(FieldName.of("second")).get());
        assertEquals(FieldValue.of(" value3"), output.getField(FieldName.of("third")).get());

        // no errors
        assertFalse(output.getError().isPresent());
    }

    @Test
    void inputFieldNotDefined() {
        Message input = Message.builder()
                .build();
        Message output = new CSVParser()
                .withDelimiter(Regex.of(","))
                .withOutputField(FieldName.of("first"), 0)
                .parse(input);

        // expect an error
        assertTrue(output.getError().isPresent());
    }

    @Test
    void outputFieldNotDefined() {
        Message input = Message.builder()
                .build();
        Message output = new CSVParser()
                .withInputField(FieldName.of("input"))
                .withDelimiter(Regex.of(","))
                .parse(input);

        // expect an error
        assertTrue(output.getError().isPresent());
    }

    @Test
    void configureInputField() {
        CSVParser parser = new CSVParser();
        parser.configure(CSVParser.inputConfig, ConfigValues.of(ConfigValue.of("input", "original_string")));
        assertEquals(FieldName.of("original_string"), parser.getInputField());
    }

    @Test
    void configureOutputFields() {
        CSVParser parser = new CSVParser();
        parser.configure(CSVParser.outputConfig, ConfigValues.of(ConfigValue.of("label", "first"), ConfigValue.of("index", "0")));
        parser.configure(CSVParser.outputConfig, ConfigValues.of(ConfigValue.of("label", "second"), ConfigValue.of("index", "1")));
        parser.configure(CSVParser.outputConfig, ConfigValues.of(ConfigValue.of("label", "third"), ConfigValue.of("index", "2")));

        List<CSVParser.OutputField> outputFields = parser.getOutputFields();
        assertEquals(3, outputFields.size());

        // first output field
        assertEquals(FieldName.of("first"), outputFields.get(0).fieldName);
        assertEquals(0, outputFields.get(0).index);

        // second output field
        assertEquals(FieldName.of("second"), outputFields.get(1).fieldName);
        assertEquals(1, outputFields.get(1).index);

        // third output field
        assertEquals(FieldName.of("third"), outputFields.get(2).fieldName);
        assertEquals(2, outputFields.get(2).index);
    }

    @Test
    void configureDelimiter() {
        CSVParser parser = new CSVParser();
        parser.configure(CSVParser.delimiterConfig, ConfigValues.of(ConfigValue.of("delimiter", "|")));
        assertEquals(Regex.of("|"), parser.getDelimiter());
    }

    @Test
    void configureTrim() {
        CSVParser parser = new CSVParser();
        parser.configure(CSVParser.trimConfig, ConfigValues.of(ConfigValue.of("trim", "false")));
        assertFalse(parser.isTrimWhitespace());
    }
}
