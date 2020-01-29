package com.cloudera.ccp.chains.parsers;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;

/**
 * The name of a configuration element
 */
public class ParserConfig {





    private final ConfigName configName;
    private final ConfigValue configValue;
    private final boolean isRequired;

    public ParserConfig(ConfigName configName, ConfigValue configValue, boolean isRequired) {
        this.configName = Objects.requireNonNull(configName);
        this.configValue = Objects.requireNonNull(configValue);
        this.isRequired = isRequired;
    }


}
