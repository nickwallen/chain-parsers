package com.cloudera.ccp.chains.parsers;

import java.util.regex.Pattern;

/**
 * A regular expression.
 */
public final class Regex {
    private final String regex;

    private Regex(String regex) {
        // a PatternSyntaxException is thrown if the pattern is not valid
        Pattern.compile(regex);
        this.regex = regex;
    }

    public static final Regex of(String regex) {
        return new Regex(regex);
    }

    /**
     * Tells whether a field value matches this regular expression.
     * @param fieldValue The value to match.
     * @return True if the field value matches the regular expression. Otherwise, false.
     */
    public boolean matches(FieldValue fieldValue) {
        return fieldValue.toString().matches(regex);
    }

    @Override
    public String toString() {
        return regex;
    }
}
