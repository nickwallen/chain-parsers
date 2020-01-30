package com.cloudera.ccp.chains;

import com.cloudera.ccp.chains.links.ChainLink;
import com.cloudera.ccp.chains.links.NextChainLink;
import com.cloudera.ccp.chains.links.RouterLink;
import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.Parser;
import com.cloudera.ccp.chains.parsers.Regex;

import java.util.Objects;

/**
 * Provides a fluent API for the construction of a parser chain.
 *
 * <code>
 * ChainLink chain = new ChainBuilder()
 *     .then(csvParser)
 *     .routeBy(routerField)
 *     .thenMatch(Regex.of("%ASA-6-302021:"), subChain)
 *     .thenMatch(Regex.of("%ASA-9-302041:"), anotherParser)
 *     .head();
 * </code>
 */
public class ChainBuilder {
    private ChainLink head;
    private NextChainLink lastLink;
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
        router = new RouterLink().withInputField(routeBy);
        if(head == null) {
            // this is a new chain starting with a router
            this.head = router;
        } else {
            // add router to the existing chain
            lastLink.withNext(router);
        }
        return this;
    }

    public ChainBuilder thenDefault(ChainLink nextLink) {
        if(router == null) {
            throw new IllegalStateException("Must call routeBy before creating a route");
        }
        router.withDefault(nextLink);
        return this;
    }

    public ChainBuilder thenDefault(Parser parser) {
        return thenDefault(new NextChainLink(parser));
    }

    public ChainBuilder thenMatch(Regex regex, ChainLink nextLink) {
        if(router == null) {
            throw new IllegalStateException("Must call routeBy before creating a route");
        }
        router.withRoute(regex, nextLink);
        return this;
    }

    public ChainBuilder thenMatch(Regex regex, Parser parser) {
        return thenMatch(regex, new NextChainLink(parser));
    }

    public ChainBuilder then(NextChainLink nextLink) {
        if(router != null) {
            throw new IllegalStateException("Cannot add another link after a router. Must define regex or default route.");
        }

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

    public ChainBuilder then(Parser parser) {
        return then(new NextChainLink(parser));
    }
}
