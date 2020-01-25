package com.cloudera.ccp.chains.parsers;

import java.util.regex.Pattern;

/**
 * A regular expression.
 */
public final class Regex {
    private final String regex;

    public Regex(String regex) {
        // a PatternSyntaxException is thrown if the pattern is not valid
        Pattern.compile(regex);
        this.regex = regex;
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
