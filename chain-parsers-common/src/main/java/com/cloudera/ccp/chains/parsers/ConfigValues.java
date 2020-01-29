package com.cloudera.ccp.chains.parsers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigValues {
    private final List<ConfigValue> configValues;

    public static ConfigValues of(ConfigValue... configValues) {
        return new ConfigValues(configValues);
    }

    private ConfigValues(ConfigValue... configValues) {
        this.configValues = Arrays.asList(configValues);
    }

    public List<ConfigValue> values() {
        return Collections.unmodifiableList(configValues);
    }

    public int size() {
        return configValues.size();
    }
}
