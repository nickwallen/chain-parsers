package com.cloudera.ccp.chains.links;

import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Parser;
import com.cloudera.ccp.chains.parsers.Regex;
import com.cloudera.ccp.chains.parsers.core.TimestampParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChainBuilderTest {
    Message message;
    Parser first;
    Parser second;

    @BeforeEach
    void setup() {
        message = Message.builder().build();
        first = new TimestampParser();
        second = new TimestampParser();
    }

    @Test
    void straightChain() {
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
    void chainWithRouter() {
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
    void routerFirstInChain() {
        ChainLink head = new ChainBuilder()
                .routeBy(FieldName.of("timestamp"))
                .then(Regex.of("[0-9]+"), second)
                .head();
        assertTrue(head instanceof Router);
    }

    @Test
    void routerWithSubChains() {
        ChainLink subChain = new ChainBuilder()
                .then(second)
                .head();

        ChainLink mainChain = new ChainBuilder()
                .then(first)
                .routeBy(FieldName.of("timestamp"))
                .then(Regex.of("[0-9]+"), subChain)
                .head();

        // validate the main chain
        assertEquals(first, mainChain.getParser());
        assertTrue(mainChain.getNext(message).isPresent());

        // validate the router
        ChainLink next = mainChain.getNext(message).get();
        assertTrue(next instanceof Router);
    }

    @Test
    void noChain() {
        assertThrows(NullPointerException.class,
                () -> new ChainBuilder().head());
    }

    @Test
    void onlyRoutesAfterRouteBy() {
        // after calling `routeBy` all subsequent calls must define a route
        assertThrows(IllegalStateException.class,
                () -> new ChainBuilder()
                        .routeBy(FieldName.of("timestamp"))
                        .then(second)
                        .head());
    }

    @Test
    void noRoutesBeforeRouteBy() {
        // cannot define a route before calling `routeBy`
        assertThrows(IllegalStateException.class,
                () -> new ChainBuilder()
                        .then(Regex.of("[0-9]+"), second)
                        .head());
    }
}
