package com.cloudera.ccp.chains.links;

import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.FieldValue;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Regex;
import com.cloudera.ccp.chains.parsers.core.NoopParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RouterLinkTest {

    @Test
    void route() {
        // there is a valid route for this message
        Message message = Message.builder()
                .addField(FieldName.of("tag"), FieldValue.of("match_1"))
                .build();

        ChainLink expectedNext = new SimpleChainLink(new NoopParser());
        RouterLink routerLink = new RouterLink()
                .withFieldName(FieldName.of("tag"))
                .withRoute(Regex.of("match_[0-9]+"), expectedNext)
                .withRoute(Regex.of("no_match"), new SimpleChainLink(new NoopParser()));

        ChainLink actualNext = routerLink.getNext(message).get();
        assertEquals(expectedNext, actualNext);
    }

    @Test
    void noRoute() {
        // there is no valid route for this message
        Message message = Message.builder()
                .addField(FieldName.of("tag"), FieldValue.of("route9"))
                .build();

        ChainLink expectedNext = new SimpleChainLink(new NoopParser());
        RouterLink routerLink = new RouterLink()
                .withFieldName(FieldName.of("tag"))
                .withRoute(Regex.of("no_match"), expectedNext);

        assertFalse(routerLink.getNext(message).isPresent());
    }

    @Test
    void defaultRoute() {
        // there is no valid route for this message
        Message message = Message.builder()
                .addField(FieldName.of("tag"), FieldValue.of("route9"))
                .build();

        ChainLink expectedNext = new SimpleChainLink(new NoopParser());
        RouterLink routerLink = new RouterLink()
                .withFieldName(FieldName.of("tag"))
                .withRoute(Regex.of("no_match"), new SimpleChainLink(new NoopParser()))
                .withDefault(expectedNext);

        ChainLink actualNext = routerLink.getNext(message).get();
        assertEquals(expectedNext, actualNext);
    }

    @Test
    void undefinedRoutingField() {
        Message message = Message.builder()
                .addField(FieldName.of("tag"), FieldValue.of("route9"))
                .build();
        RouterLink routerLink = new RouterLink()
                .withRoute(Regex.of("match"), new SimpleChainLink(new NoopParser()));

        // the field used to route the message was not defined
        assertThrows(IllegalStateException.class, () -> routerLink.getNext(message));
    }
}
