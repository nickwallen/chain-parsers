package com.cloudera.ccp.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParserChainRunner {

    public List<Message> run(Message message, ChainLink head) {
        List<Message> results = new ArrayList<>();
        results.add(message);

        Optional<ChainLink> next = Optional.of(head);
        do {
            Message input = results.get(results.size()-1);
            Parser parser = next.get().getParser();
            Message output = parser.parse(input);
            results.add(output);
            next = next.get().getNext();

        } while(next.isPresent());

        return results;
    }

    public static void main(String[] args) {
        FieldName originalField = new FieldName("original_string");
        FieldName routerField = new FieldName("asa_tag");
        FieldName timestampField = new FieldName("processing_time");

        // the message that needs parsed
        String input = "%ASA-6-302021: Teardown ICMP connection for faddr 10.22.8.74/0(LOCAL\\user.name) gaddr 10.22.8.205/0 laddr 10.22.8.205/0";
        Message message = Message.builder()
                .addField(originalField, new FieldValue(input))
                .build();

        // a csv parser is used to extract the 'asa_tag' for routing
        CSVParser csvParser = new CSVParser()
                .withInputField(originalField)
                .withDelimiter(new Regex("\\s+"))
                .withOutputField(routerField, 0);

        // timestamps the message with 'processing_time'
        TimestampParser timestampParser = new TimestampParser()
                .withFieldName(timestampField);

        // CSV -> Router -> Timestamp
        // TODO does this have to be so ugly?
//        ChainLink chain = ChainBuilder()
//                .then(csvParser)
//                .then("%ASA-6-302021:", timestampParser)
//                .head();

        SimpleChainLink first = new SimpleChainLink(csvParser);
        Router second = new Router()
                .withFieldName(routerField);
        ChainLink third = new SimpleChainLink(timestampParser);
        first.setNext(second);
        second.withRoute("%ASA-6-302021:", third);

        // run the parser chain
        ParserChainRunner runner = new ParserChainRunner();
        List<Message> results = runner.run(message, first);
        for(Message result: results) {
            System.out.println("result: " + result);
        }
    }
}
