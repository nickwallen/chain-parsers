package com.cloudera.ccp.chains.links;

import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Parser;

import java.util.Optional;

public interface ChainLink {
    Parser getParser();
    Optional<ChainLink> getNext(Message message);
}
