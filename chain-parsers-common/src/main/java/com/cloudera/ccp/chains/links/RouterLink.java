package com.cloudera.ccp.chains.links;

import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.FieldValue;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.core.NoopParser;
import com.cloudera.ccp.chains.parsers.Parser;
import com.cloudera.ccp.chains.parsers.Regex;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Allows messages to be parsed differently based on values contained
 * within a message.
 */
public class RouterLink implements ChainLink {

    /**
     * Defines one route that a {@link Message} may take.
     */
    private static class Route {
        private final Regex regex;
        private final ChainLink next;

        public Route(Regex regex, ChainLink next) {
            this.regex = Objects.requireNonNull(regex);
            this.next = Objects.requireNonNull(next);
        }
    }

    /**
     * The name of the field whose value is used for routing.
     */
    private FieldName routingField;
    private List<Route> routes;
    private Optional<ChainLink> defaultRoute;

    public RouterLink() {
        this.routes = new ArrayList<>();
        this.defaultRoute = Optional.empty();
    }

    public RouterLink withFieldName(FieldName fieldName) {
        routingField = Objects.requireNonNull(fieldName);
        return this;
    }

    public RouterLink withRoute(Regex regex, ChainLink next) {
        routes.add(new Route(regex, next));
        return this;
    }

    public RouterLink withDefault(ChainLink defaultNext) {
        this.defaultRoute = Optional.of(defaultNext);
        return this;
    }

    @Override
    public Parser getParser() {
        // no parsing is performed when routing
        return new NoopParser();
    }

    @Override
    public Optional<ChainLink> getNext(Message input) {
        if(routingField == null) {
            throw new IllegalStateException("The routing field was not defined.");
        }

        Optional<FieldValue> valueOpt = input.getField(routingField);
        if(valueOpt.isPresent()) {
            FieldValue fieldValue = valueOpt.get();

            for(Route route: routes) {
                Regex regex = route.regex;
                if(regex.matches(fieldValue)) {
                    return Optional.of(route.next);
                }
            }
        }

        // no routes matched, use the default next link if one is present
        return defaultRoute;
    }
}
