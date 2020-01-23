package com.cloudera.ccp.parsers;

import java.util.regex.Pattern;

/**
 * A regular expression.
 */
public class Regex {
    private String regex;

    public Regex(String regex) {
        // a PatternSyntaxException is thrown if the pattern is not valid
        Pattern.compile(regex);
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}
