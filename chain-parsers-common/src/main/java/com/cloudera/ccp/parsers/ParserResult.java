package com.cloudera.ccp.parsers;

public class ParserResult {
    private Message message;
    private Parser parser;
    private Throwable error;

    private ParserResult(Message message, Parser parser, Throwable error) {
        this.message = message;
        this.parser = parser;
        this.error = error;
    }

    public static ParserResult success(Message message) {
        return new ParserResult(message, null, null);
    }

    public static ParserResult success(Message message, Parser parser) {
        return new ParserResult(message, parser, null);
    }

    public static ParserResult error(Message message, Parser parser, Throwable cause) {
        return new ParserResult(message, parser, cause);
    }

    public Message getMessage() {
        return message;
    }

    public Parser getParser() {
        return parser;
    }

    public Throwable getError() {
        return error;
    }
}
