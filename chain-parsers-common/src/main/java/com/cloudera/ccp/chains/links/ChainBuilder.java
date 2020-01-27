package com.cloudera.ccp.chains.links;

import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.Parser;
import com.cloudera.ccp.chains.parsers.Regex;

import java.util.Objects;

/**
 * Simplifies the construction of a parser chain.
 */
public class ChainBuilder {
    private ChainLink head;
    private SimpleChainLink lastLink;
    private RouterLink router;

    /**
     * Returns the head of the chain.
     * @return The first link in the chain.
     */
    public ChainLink head() {
        return Objects.requireNonNull(head, "No chain defined yet.");
    }

    public ChainBuilder routeBy(FieldName routeBy) {
        // create the router
        router = new RouterLink().withFieldName(routeBy);
        if(head == null) {
            // this is a new chain starting with a router
            this.head = router;
        } else {
            // add router to the existing chain
            lastLink.withNext(router);
        }
        return this;
    }

    public ChainBuilder then(Regex regex, ChainLink nextLink) {
        if(router == null) {
            throw new IllegalStateException("Must call routeBy before creating a route");
        }
        router.withRoute(regex, nextLink);
        return this;
    }

    public ChainBuilder then(Regex regex, Parser parser) {
        if(router == null) {
            throw new IllegalStateException("Must call routeBy before creating a route");
        }
        router.withRoute(regex, new SimpleChainLink(parser));
        return this;
    }

    public ChainBuilder then(Parser parser) {
        if(router != null) {
            throw new IllegalStateException("Cannot add another simple link after a router");
        }
        return then(new SimpleChainLink(parser));
    }

    public ChainBuilder then(SimpleChainLink nextLink) {
        if(head == null) {
            // this is a new chain
            this.head = nextLink;
            this.lastLink = nextLink;

        } else {
            // add link to the existing chain
            lastLink.withNext(nextLink);
            lastLink = nextLink;
        }

        return this;
    }
}
