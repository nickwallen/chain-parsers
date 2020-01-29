package com.cloudera.ccp.chains;

import com.cloudera.ccp.chains.links.ChainBuilder;
import com.cloudera.ccp.chains.links.ChainLink;
import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.FieldValue;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Parser;
import com.cloudera.ccp.chains.parsers.Regex;
import com.cloudera.ccp.chains.parsers.core.CSVParser;
import com.cloudera.ccp.chains.parsers.core.TimestampParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParserChainRunner {

    /**
     * Parses a message using a parser chain.
     * @param message The message to parse.
     * @param chain The parser chain that performs the parsing.
     * @return
     */
    public List<Message> run(Message message, ChainLink chain) {
        List<Message> results = new ArrayList<>();
        results.add(message);

        Optional<ChainLink> next = Optional.of(chain);
        do {
            Message input = results.get(results.size()-1);
            Parser parser = next.get().getParser();
            Message output = parser.parse(input);
            results.add(output);
            next = next.get().getNext(output);

        } while(next.isPresent());

        return results;
    }

    public static void main(String[] args) {
        FieldName originalField = FieldName.of("original_string");
        FieldName routerField = FieldName.of("asa_tag");
        FieldName timestampField = FieldName.of("processing_time");

        // a csv parser is used to extract the 'asa_tag' for routing
        CSVParser csvParser = new CSVParser()
                .withInputField(originalField)
                .withDelimiter(Regex.of("\\s+"))
                .withOutputField(routerField, 0);

        // timestamps the message with 'processing_time'
        TimestampParser timestampParser = new TimestampParser()
                .withOutputField(timestampField);

        // CSV -> Router -> Timestamp
        ChainLink chain = new ChainBuilder()
                .then(csvParser)
                .routeBy(routerField)
                .then(Regex.of("%ASA-6-302021:"), timestampParser)
                .head();

        // a message that needs to be timestamped
        String input1 = "%ASA-6-302021: Teardown ICMP connection for faddr 10.22.8.74/0(LOCAL\\user.name) gaddr 10.22.8.205/0 laddr 10.22.8.205/0";
        Message message1 = Message.builder()
                .addField(originalField, FieldValue.of(input1))
                .build();

        ParserChainRunner runner = new ParserChainRunner();
        for(Message result: runner.run(message1, chain)) {
            System.out.println("message1: " + result);
        }

        // a message that will not get timestamped
        String input2 = "%ASA-9-102021: Teardown ICMP connection for faddr 10.22.8.74/0(LOCAL\\user.name) gaddr 10.22.8.205/0 laddr 10.22.8.205/0";
        Message message2 = Message.builder()
                .addField(originalField, FieldValue.of(input2))
                .build();

        for(Message result: runner.run(message2, chain)) {
            System.out.println("message2: " + result);
        }
    }
}
