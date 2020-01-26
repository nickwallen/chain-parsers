package com.cloudera.ccp.chains.parsers;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldValue that = (FieldValue) o;
        return new EqualsBuilder()
                .append(value, that.value)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(value)
                .toHashCode();
    }
}
