package com.cloudera.ccp.chains.parsers;

import java.util.List;

/**
 * Parses a {@link Message}.
 */
public interface Parser {

    /**
     * Parse a {@link Message}.
     * @param message The message to parse.
     * @return A parsed message.
     */
    Message parse(Message message);

    /**
     * Returns the known output fields added to all parsed messages.
     */
    List<FieldName> outputFields();

    /**
     * Returns the configuration elements expected by the parser.
     * @return A list of valid configuration elements.
     */
    List<ConfigName> validConfigurations();

    /**
     * Configure a parser. Expect this method to be called for each {@link ConfigName} value.
     *
     * @param configName The name of the configuration.
     * @param configValues The value(s) of the configuration element.
     */
    void configure(ConfigName configName, ConfigValues configValues);
}
