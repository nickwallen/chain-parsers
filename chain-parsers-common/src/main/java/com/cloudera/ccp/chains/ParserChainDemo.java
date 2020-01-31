package com.cloudera.ccp.chains;

import com.cloudera.ccp.chains.links.ChainLink;
import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.FieldValue;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Regex;
import com.cloudera.ccp.chains.parsers.core.AlwaysFailParser;
import com.cloudera.ccp.chains.parsers.core.DelimitedTextParser;
import com.cloudera.ccp.chains.parsers.core.RemoveFieldParser;
import com.cloudera.ccp.chains.parsers.core.TimestampParser;
import org.apache.commons.collections.MapUtils;

import java.util.List;

public class ParserChainDemo {

    public static void main(String[] args) {
        ChainLink chain = buildParserChain();

        // a message that needs to be parsed
        String input1 = "%ASA-6-302021: Teardown ICMP connection for faddr 10.22.8.74/0(LOCAL\\user.name) gaddr 10.22.8.205/0 laddr 10.22.8.205/0";
        Message message1 = Message.builder()
                .addField(FieldName.of("original_string"), FieldValue.of(input1))
                .build();

        System.out.println("\n\n----> Using a route to add a timestamp to some messages <-----");
        ChainRunner runner = new ChainRunner();
        List<Message> results1 = runner.run(message1, chain);
        printResults(results1);

        // a message that will not get timestamped
        String input2 = "%ASA-9-102021: Teardown ICMP connection for faddr 10.22.8.74/0(LOCAL\\user.name) gaddr 10.22.8.205/0 laddr 10.22.8.205/0";
        Message message2 = Message.builder()
                .addField(FieldName.of("original_string"), FieldValue.of(input2))
                .build();

        System.out.println("\n\n----> Using a parser chain to remove 'asa_tag' <-----");
        List<Message> results2 = runner.run(message2, chain);
        printResults(results2);

        // a message that will not match and take the default route and have the 'asa_tag' removed
        String input3 = "%ASA-9-9999999: Teardown ICMP connection for faddr 10.22.8.74/0(LOCAL\\user.name) gaddr 10.22.8.205/0 laddr 10.22.8.205/0";
        Message message3 = Message.builder()
                .addField(FieldName.of("original_string"), FieldValue.of(input3))
                .build();

        System.out.println("\n\n----> Using a default route to error on unexpected values <-----");
        List<Message> results3 = runner.run(message3, chain);
        printResults(results3);
    }

    private static void printResults(List<Message> results) {
        int i = 1;
        for(Message result: results) {
            if(!result.getError().isPresent()) {
                MapUtils.verbosePrint(System.out, String.format("Message @ Step %d", i), result.getFields());
            } else {
                MapUtils.verbosePrint(System.out, String.format("Error @ Step %d, %s", i, result.getError().get().getMessage()), result.getFields());
            }
            i++;
        }
    }

    private static ChainLink buildParserChain() {
        // extracts the 'asa_tag' for routing
        DelimitedTextParser delimitedTextParser = new DelimitedTextParser()
                .withInputField(FieldName.of("original_string"))
                .withDelimiter(Regex.of("\\s+"))
                .withOutputField(FieldName.of("asa_tag"), 0);

        // timestamps the message with 'processing_time'
        TimestampParser timestampParser = new TimestampParser()
                .withOutputField(FieldName.of("processing_time"));

        // will flag when an unexpected 'asa_tag' is encountered
        AlwaysFailParser alwaysFailParser = new AlwaysFailParser()
                .withError("Unexpected 'asa_tag'");

        // removes the 'asa_tag' from message that don't need it
        RemoveFieldParser removeFieldParser = new RemoveFieldParser()
                .removeField(FieldName.of("asa_tag"));

        // build the parser chain
        return new ChainBuilder()
                .then(delimitedTextParser)
                .routeBy(FieldName.of("asa_tag"))
                .thenMatch(Regex.of("%ASA-6-302021:"), timestampParser)
                .thenMatch(Regex.of("%ASA-9-102021:"), removeFieldParser)
                .thenDefault(alwaysFailParser)
                .head();
    }
}
