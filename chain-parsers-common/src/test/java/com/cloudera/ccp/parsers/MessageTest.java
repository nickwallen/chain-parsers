package com.cloudera.ccp.parsers;

import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.FieldValue;
import com.cloudera.ccp.chains.parsers.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MessageTest {

    @Test
    void addField() {
        Message message = Message.builder()
                .addField(FieldName.of("field1"), FieldValue.of("value1"))
                .addField(FieldName.of("field2"), FieldValue.of("value2"))
                .addField(FieldName.of("field3"), FieldValue.of("value3"))
                .build();

        // validate presence
        assertEquals(FieldValue.of("value1"), message.getField(FieldName.of("field1")).get());
        assertEquals(FieldValue.of("value2"), message.getField(FieldName.of("field2")).get());
        assertEquals(FieldValue.of("value3"), message.getField(FieldName.of("field3")).get());

        // validate absence
        assertFalse(message.getField(FieldName.of("field4")).isPresent());
        assertFalse(message.getField(FieldName.of("field5")).isPresent());
        assertFalse(message.getField(FieldName.of("field6")).isPresent());

        // no errors
        assertFalse(message.getError().isPresent());
    }

    @Test
    void removeField() {
        Message original = Message.builder()
                .addField(FieldName.of("field1"), FieldValue.of("value1"))
                .addField(FieldName.of("field2"), FieldValue.of("value2"))
                .addField(FieldName.of("field3"), FieldValue.of("value3"))
                .build();

        Message copy = Message.builder()
                .withFields(original)
                .removeField(FieldName.of("field1"))
                .build();

        // validate presence
        assertEquals(FieldValue.of("value2"), original.getField(FieldName.of("field2")).get());
        assertEquals(FieldValue.of("value3"), original.getField(FieldName.of("field3")).get());

        // validate absence
        assertFalse(copy.getField(FieldName.of("field1")).isPresent());

        // no errors
        assertFalse(copy.getError().isPresent());
    }

    @Test
    void withFields() {
        Message original = Message.builder()
                .addField(FieldName.of("field1"), FieldValue.of("value1"))
                .addField(FieldName.of("field2"), FieldValue.of("value2"))
                .addField(FieldName.of("field3"), FieldValue.of("value3"))
                .build();

        Message copy = Message.builder()
                .withFields(original)
                .build();

        // validate presence
        assertEquals(FieldValue.of("value1"), copy.getField(FieldName.of("field1")).get());
        assertEquals(FieldValue.of("value2"), copy.getField(FieldName.of("field2")).get());
        assertEquals(FieldValue.of("value3"), copy.getField(FieldName.of("field3")).get());

        // validate absence
        assertFalse(copy.getField(FieldName.of("field4")).isPresent());
        assertFalse(copy.getField(FieldName.of("field5")).isPresent());
        assertFalse(copy.getField(FieldName.of("field6")).isPresent());

        // no errors
        assertFalse(copy.getError().isPresent());
    }

    @Test
    void withErrorMessage() {
        final String errorMessage = "this is an error";
        Message original = Message.builder()
                .withError(errorMessage)
                .build();
        assertEquals(errorMessage, original.getError().get().getMessage());
    }

    @Test
    void withError() {
        final Exception exception = new IllegalStateException("this is an error");
        Message original = Message.builder()
                .withError(exception)
                .build();
        assertEquals(exception, original.getError().get());
    }
}
