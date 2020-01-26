package com.cloudera.ccp.chains.parsers;

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
}
