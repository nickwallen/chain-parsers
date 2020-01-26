package com.cloudera.ccp.chains.parsers;

import java.util.Objects;

/**
 * The value of a field contained within a {@link Message}.
 */
public class FieldValue {
    // TODO does this need to be bytes?
    private final String value;

    public static FieldValue of(String fieldValue) {
        return new FieldValue(fieldValue);
    }

    /**
     * Use {@link FieldValue#of(String)}.
     */
    private FieldValue(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public String toString() {
        return value;
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
