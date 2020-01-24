package com.cloudera.ccp.chains.links;

import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.Parser;
import com.cloudera.ccp.chains.parsers.Regex;

public class ChainBuilder {

    // CSV -> Router -> Timestamp
    // TODO does this have to be so ugly?
    //        ChainLink chain = ChainBuilder()
    //                .then(csvParser)
    //                .then(asaTypeField, "%ASA-6-302021:", timestampParser)
    //                .head();

    //
    //  ChainLink chain = ChainBuilder()
    //          .routeBy(asaType)
    //          .then("%ASA-6-302021:", timestampParser)
    //          .then("%ASA-9-302021:", otherParser)

    //
    // ChainLink asa9Chain = ChainBuilder()
    //          .then(csvParser)
    //          .then(timestampParser)
    //
    // ChainLink mainChain = ChainBuilder()
    //          .routeBy(asaType)
    //          .then("%ASA-9-302021:", asa9Chain)
    //

    private ChainLink head;
    private SimpleChainLink lastLink;
    private Router router;

    public ChainLink head() {
        return head;
    }

    public ChainBuilder routeBy(FieldName routeBy) {
        router = new Router()
                .withFieldName(routeBy);

        // TODO need to connect last link to the router
        // add router to the existing chain
        lastLink.setNext(router);
//        lastLink = router;
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
            throw new IllegalArgumentException("Cannot add another simple link after a router");
        }
        return then(new SimpleChainLink(parser));
    }

    public ChainBuilder then(SimpleChainLink link) {
        if(head == null) {
            // this is a new chain
            this.head = link;
            this.lastLink = link;

        } else {
            // add link to the existing chain
            lastLink.setNext(link);
            lastLink = link;
        }

        return this;
    }
}
