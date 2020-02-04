package com.cloudera.ccp.chains.parsers.core;

import com.cloudera.ccp.chains.parsers.ConfigName;
import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.FieldValue;
import com.cloudera.ccp.chains.parsers.Message;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RemoveFieldParserTest {

    @Test
    void removeField() {
        Message input = Message.builder()
                .addField(FieldName.of("field1"), FieldValue.of("value1"))
                .addField(FieldName.of("field2"), FieldValue.of("value2"))
                .addField(FieldName.of("field3"), FieldValue.of("value3"))
                .build();
        Message output = new RemoveFieldParser()
                .removeField(FieldName.of("field1"))
                .removeField(FieldName.of("field2"))
                .parse(input);

        // ensure the fields were removed
        assertFalse(output.getField(FieldName.of("field1")).isPresent());
        assertFalse(output.getField(FieldName.of("field2")).isPresent());
        assertEquals(FieldValue.of("value3"), output.getField(FieldName.of("field3")).get());
    }

    @Test
    void nothingToRemove() {
        Message input = Message.builder()
                .addField(FieldName.of("field1"), FieldValue.of("value1"))
                .addField(FieldName.of("field2"), FieldValue.of("value2"))
                .addField(FieldName.of("field3"), FieldValue.of("value3"))
                .build();
        Message output = new RemoveFieldParser()
                .parse(input);

        // ensure the fields were removed
        assertEquals(FieldValue.of("value1"), output.getField(FieldName.of("field1")).get());
        assertEquals(FieldValue.of("value2"), output.getField(FieldName.of("field2")).get());
        assertEquals(FieldValue.of("value3"), output.getField(FieldName.of("field3")).get());
    }

    @Test
    void unexpectedConfig() {
        assertThrows(IllegalArgumentException.class,
                () -> new RemoveFieldParser().configure(ConfigName.of("invalid", false), Collections.emptyList()));
    }
}
