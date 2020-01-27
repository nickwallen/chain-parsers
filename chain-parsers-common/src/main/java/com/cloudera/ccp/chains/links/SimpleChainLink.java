package com.cloudera.ccp.chains.links;

import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Parser;

import java.util.Optional;

/**
 * A {@link ChainLink} that links directly to the next link in a chain.
 */
public class SimpleChainLink implements ChainLink {
    private Parser parser;
    private Optional<ChainLink> next;

    public SimpleChainLink(Parser parser) {
        this.parser = parser;
        this.next = Optional.empty();
    }

    public Parser getParser() {
        return parser;
    }

    /**
     * Get the next link in the chain.
     * @param message The message that is being parsed.
     * @return The next link in the chain or Optional.empty if there is no next link.
     */
    @Override
    public Optional<ChainLink> getNext(Message message) {
        return next;
    }

    public SimpleChainLink withNext(ChainLink next) {
        this.next = Optional.of(next);
        return this;
    }
}
