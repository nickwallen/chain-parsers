package com.cloudera.ccp.chains.parsers;

import java.util.Objects;

/**
 * The value of a field contained within a {@link Message}.
 */
public class FieldValue {
    private String value;

    // TODO does this need to be bytes?

    public FieldValue(String value) {
        this.value = value;
    }

    public String getString() {
        return value;
    }

    @Override
    public String toString() {
        return "FieldValue{" + value +  '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldValue that = (FieldValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
