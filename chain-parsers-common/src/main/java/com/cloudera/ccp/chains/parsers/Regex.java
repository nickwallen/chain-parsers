package com.cloudera.ccp.chains.parsers;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A regular expression.
 */
public final class Regex {
    private final String regex;
    private final Pattern pattern;

    public static final Regex of(String regex) {
        return new Regex(regex);
    }

    private Regex(String regex) {
        this.regex = Objects.requireNonNull(regex);
        this.pattern = Pattern.compile(regex);
    }

    /**
     * Tells whether a field value matches this regular expression.
     * @param fieldValue The value to match.
     * @return True if the field value matches the regular expression. Otherwise, false.
     */
    public boolean matches(FieldValue fieldValue) {
        return pattern.matcher(fieldValue.toString()).matches();
    }

    @Override
    public String toString() {
        return regex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Regex that = (Regex) o;
        return new EqualsBuilder()
                .append(regex, that.regex)
                .append(pattern, that.pattern)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(regex)
                .append(pattern)
                .toHashCode();
    }
}
