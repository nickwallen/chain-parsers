package com.cloudera.ccp.parsers;

import java.util.Optional;

public interface ChainLink {
    Parser getParser();
    Optional<ChainLink> getNext();
}
