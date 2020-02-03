package com.cloudera.ccp.chains.parsers;

import java.util.List;

/**
 * Provides a catalog of all parsers available to the user.
 */
public interface ParserCatalog {

    /**
     * Returns all of the available parsers in the catalog.
     */
    List<MessageParser> getParsers();
}