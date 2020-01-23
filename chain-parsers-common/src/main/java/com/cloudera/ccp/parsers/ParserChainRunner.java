package com.cloudera.ccp.parsers;

import java.util.Optional;

public class ParserChainRunner {

    public ParserResult run(Message message, ChainLink head) {
        Optional<ChainLink> next = Optional.of(head);
        Message latest = message;
        do {
            Parser parser = next.get().getParser();
            ParserResult result = parser.parse(latest);

            next = next.get().getNext();
            latest = result.getMessage();

        } while(next.isPresent());

        return ParserResult.success(message);
    }


    public static void main(String[] args) {
        FieldName originalField = new FieldName("original_string");
        FieldName routerField = new FieldName("asa_tag");
        FieldName timestampField = new FieldName("processing_time");

        String input = "%ASA-6-302021: Teardown ICMP connection for faddr 10.22.8.74/0(LOCAL\\user.name) gaddr 10.22.8.205/0 laddr 10.22.8.205/0";
        Message message = new Message()
                .withField(originalField, new FieldValue(input));

        CSVParser csvParser = new CSVParser()
                .withInputField(originalField)
                .withDelimiter("\\s+")
                .withLabel(routerField, 0);

        TimestampParser timestampParser = new TimestampParser()
                .withFieldName(timestampField);

        SimpleChainLink first = new SimpleChainLink(csvParser);
        Router second = new Router()
                .withFieldName(routerField);
        ChainLink third = new SimpleChainLink(timestampParser);

        first.setNext(second);
        second.withRoute("%ASA-6-302021:", third);

        ParserChainRunner runner = new ParserChainRunner();
        ParserResult result = runner.run(message, first);

        System.out.println(result.getMessage());

    }
}
