package com.cloudera.ccp.parsers;

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
    public Optional<ChainLink> getNext() {
        return next;
    }

    public void setNext(ChainLink next) {
        this.next = Optional.of(next);
    }
}
