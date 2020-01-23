package com.cloudera.ccp.parsers;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * The name of a field contained within a {@link Message}.
 */
public class FieldName {
    private static final String VALID_FIELD_NAME_REGEX = "[\\w\\d-_. ]+";
    private String fieldName;

    public FieldName(String fieldName) {
        if(StringUtils.isBlank(fieldName) || !fieldName.matches(VALID_FIELD_NAME_REGEX)) {
            throw new IllegalArgumentException(String.format("Invalid field name: '%s'", fieldName));
        }
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldName other = (FieldName) o;
        return Objects.equals(fieldName, other.fieldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName);
    }

    @Override
    public String toString() {
        return "FieldName{" + fieldName + '}';
    }
}