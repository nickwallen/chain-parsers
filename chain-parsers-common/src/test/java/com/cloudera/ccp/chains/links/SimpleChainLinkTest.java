package com.cloudera.ccp.chains.links;

import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Parser;
import com.cloudera.ccp.chains.parsers.core.NoopParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SimpleChainLinkTest {
    Message message = Message.builder().build();

    @Test
    void nextLink() {
        SimpleChainLink next = new SimpleChainLink(new NoopParser());
        SimpleChainLink previous = new SimpleChainLink(new NoopParser())
                .withNext(next);
        assertEquals(next, previous.getNext(message).get());
    }

    @Test
    void nextLinkEmpty() {
        SimpleChainLink link = new SimpleChainLink(new NoopParser());
        // the next link is not present until set
        assertFalse(link.getNext(message).isPresent());
    }

    @Test
    void getParser() {
        Parser parser = new NoopParser();
        SimpleChainLink link = new SimpleChainLink(parser);
        assertEquals(parser, link.getParser());
    }
}
