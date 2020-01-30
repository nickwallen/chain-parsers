package com.cloudera.ccp.chains;

import com.cloudera.ccp.chains.ChainBuilder;
import com.cloudera.ccp.chains.links.ChainLink;
import com.cloudera.ccp.chains.links.RouterLink;
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
    Parser firstParser;
    Parser secondParser;

    @BeforeEach
    void setup() {
        message = Message.builder().build();
        firstParser = new TimestampParser();
        secondParser = new TimestampParser();
    }

    @Test
    void straightChain() {
        ChainLink head = new ChainBuilder()
                .then(firstParser)
                .then(secondParser)
                .head();

        // validate the first link
        assertEquals(firstParser, head.getParser());
        assertTrue(head.getNext(message).isPresent());

        // validate the second link
        ChainLink next = head.getNext(message).get();
        assertEquals(secondParser, next.getParser());
        assertFalse(next.getNext(message).isPresent());
    }

    @Test
    void chainWithRouter() {
        ChainLink head = new ChainBuilder()
                .then(firstParser)
                .routeBy(FieldName.of("timestamp"))
                .thenMatch(Regex.of("[0-9]+"), secondParser)
                .head();

        // validate the first link
        assertEquals(firstParser, head.getParser());
        assertTrue(head.getNext(message).isPresent());

        // validate the router
        ChainLink next = head.getNext(message).get();
        assertTrue(next instanceof RouterLink);
    }

    @Test
    void routerFirstInChain() {
        ChainLink head = new ChainBuilder()
                .routeBy(FieldName.of("timestamp"))
                .thenMatch(Regex.of("[0-9]+"), secondParser)
                .head();
        assertTrue(head instanceof RouterLink);
    }

    @Test
    void routerWithSubChains() {
        ChainLink subChain = new ChainBuilder()
                .then(secondParser)
                .head();

        ChainLink mainChain = new ChainBuilder()
                .then(firstParser)
                .routeBy(FieldName.of("timestamp"))
                .thenMatch(Regex.of("[0-9]+"), subChain)
                .head();

        // validate the main chain
        assertEquals(firstParser, mainChain.getParser());
        assertTrue(mainChain.getNext(message).isPresent());

        // validate the router
        ChainLink next = mainChain.getNext(message).get();
        assertTrue(next instanceof RouterLink);
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
                        .then(secondParser)
                        .head());
    }

    @Test
    void noRoutesBeforeRouteBy() {
        // cannot define a route before calling `routeBy`
        assertThrows(IllegalStateException.class,
                () -> new ChainBuilder()
                        .thenMatch(Regex.of("[0-9]+"), secondParser)
                        .head());
    }
}