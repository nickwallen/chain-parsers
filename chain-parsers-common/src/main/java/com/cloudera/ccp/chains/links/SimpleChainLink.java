package com.cloudera.ccp.chains.links;

import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Parser;

import java.util.Optional;

public class SimpleChainLink implements ChainLink {
    private Parser parser;
    private Optional<ChainLink> next;

    public SimpleChainLink(Parser parser, ChainLink next) {
        this.parser = parser;
        this.next = Optional.of(next);
    }

    public SimpleChainLink(Parser parser) {
        this.parser = parser;
        this.next = Optional.empty();
    }

    public Parser getParser() {
        return parser;
    }

    @Override
    public Optional<ChainLink> getNext(Message message) {
        return next;
    }

    public void setNext(ChainLink next) {
        this.next = Optional.of(next);
    }
}
