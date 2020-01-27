package com.cloudera.ccp.chains;

import com.cloudera.ccp.chains.links.ChainBuilder;
import com.cloudera.ccp.chains.links.ChainLink;
import com.cloudera.ccp.chains.links.Router;
import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Parser;
import com.cloudera.ccp.chains.parsers.Regex;
import com.cloudera.ccp.chains.parsers.core.TimestampParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChainBuilderTest {

    @Test
    void simpleChain() {
        Message message = Message.builder().build();
        Parser first = new TimestampParser();
        Parser second = new TimestampParser();

        ChainLink head = new ChainBuilder()
                .then(first)
                .then(second)
                .head();

        // validate the first link
        assertEquals(first, head.getParser());
        assertTrue(head.getNext(message).isPresent());

        // validate the second link
        ChainLink next = head.getNext(message).get();
        assertEquals(second, next.getParser());
        assertFalse(next.getNext(message).isPresent());
    }

    @Test
    void withRouter() {
        Message message = Message.builder().build();
        Parser first = new TimestampParser();
        Parser second = new TimestampParser();

        ChainLink head = new ChainBuilder()
                .then(first)
                .routeBy(FieldName.of("timestamp"))
                .then(Regex.of("[0-9]+"), second)
                .head();

        // validate the first link
        assertEquals(first, head.getParser());
        assertTrue(head.getNext(message).isPresent());

        // validate the router
        ChainLink next = head.getNext(message).get();
        assertTrue(next instanceof Router);
    }

    @Test
    void twoChains() {
        Message message = Message.builder().build();
        Parser first = new TimestampParser();
        Parser second = new TimestampParser();

        ChainLink subChain = new ChainBuilder()
                .then(second)
                .head();

        ChainLink head = new ChainBuilder()
                .then(first)
                .routeBy(FieldName.of("timestamp"))
                .then(Regex.of("[0-9]+"), subChain)
                .head();

        // validate the first link
        assertEquals(first, head.getParser());
        assertTrue(head.getNext(message).isPresent());

        // validate the router
        ChainLink next = head.getNext(message).get();
        assertTrue(next instanceof Router);
    }
}
