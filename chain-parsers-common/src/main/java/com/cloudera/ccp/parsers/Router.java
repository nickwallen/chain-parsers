package com.cloudera.ccp.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Allows a message to be parsed differently based on values contained
 * within the message.
 *
 * A {@link Router} is both a {@link Parser} and a {@link ChainLink} because
 * it must parse the message to determine the next {@link ChainLink} in the chain.
 */
public class Router implements Parser, ChainLink {

    public static class Route {
        private String regex;
        private ChainLink next;

        public Route(String regex, ChainLink next) {
            this.regex = regex;
            this.next = next;
        }

        public String getRegex() {
            return regex;
        }

        public ChainLink getNext() {
            return next;
        }
    }

    /**
     * The name of the field whose value is used for routing.
     */
    private FieldName routingField;
    private List<Route> routes;
    private FieldValue fieldValue;

    public Router() {
        this.routes = new ArrayList<>();
    }

    public Router withFieldName(FieldName fieldName) {
        routingField = fieldName;
        return this;
    }

    public Router withRoute(String regex, ChainLink next) {
        routes.add(new Route(regex, next));
        return this;
    }

    @Override
    public ParserResult parse(Message message) {
        if(routingField == null) {
            throw new IllegalStateException("The routing field was not defined.");
        }

        Optional<FieldValue> valueOpt = message.getField(routingField);
        if(valueOpt.isPresent()) {
            fieldValue = valueOpt.get();
            return ParserResult.success(message, this);
        } else {
            return ParserResult.error(message, this,
                    new IllegalStateException(String.format("Missing expected field: '%s'", routingField.getFieldName())));
        }

        // TODO define a "default" route
        // TODO if no match and no default route, then error
    }

    @Override
    public List<FieldName> outputFields() {
        // no output fields are defined by the router
        return Collections.emptyList();
    }

    @Override
    public Parser getParser() {
        return this;
    }

    @Override
    public Optional<ChainLink> getNext() {
        for(Route route: routes) {
            if(fieldValue.getString().matches(route.getRegex())) {
                return Optional.of(route.getNext());
            }
        }

        // TODO log debug no routes match, nothing to do
        return Optional.empty();
    }

//    @Override
//    public void setNext(ChainLink next) {
//        // TODO doesnt make sense for the Router
//    }
}
