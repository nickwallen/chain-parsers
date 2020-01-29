package com.cloudera.ccp.chains.parsers;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 */
public class ConfigValue {
    private static final Regex isValidRegex = Regex.of("[\\w\\d-_.,|\\]\\[]+");
    private String key;
    private String value;

    private ConfigValue(String key, String value) {
        if(!isValidRegex.matches(key)) {
            throw new IllegalArgumentException(String.format("Invalid config key: '%s'", value));
        }
        this.key = key;

        if(!isValidRegex.matches(value)) {
            throw new IllegalArgumentException(String.format("Invalid config value: '%s'", value));
        }
        this.value = value;
    }

    /**
     * Create a {@link ConfigValue} with only a value, no key.
     * @param value The value.
     */
    public static ConfigValue of(String value) {
        // TODO fix this, will not pass validation
        return new ConfigValue(null, value);
    }

    public static ConfigValue of(String key, String value) {
        return new ConfigValue(key, value);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ConfigValue{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConfigValue that = (ConfigValue) o;
        return new EqualsBuilder()
                .append(key, that.key)
                .append(value, that.value)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(key)
                .append(value)
                .toHashCode();
    }
}