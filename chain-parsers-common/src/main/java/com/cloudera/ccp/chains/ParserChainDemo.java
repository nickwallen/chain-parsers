package com.cloudera.ccp.chains;

import com.cloudera.ccp.chains.links.ChainLink;
import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.FieldValue;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Regex;
import com.cloudera.ccp.chains.parsers.core.AlwaysFailParser;
import com.cloudera.ccp.chains.parsers.core.CSVParser;
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

        System.out.println("\n\nParsing a message that should get timestamped...");
        ChainRunner runner = new ChainRunner();
        List<Message> results1 = runner.run(message1, chain);
        printResults(results1);

        // a message that will not get timestamped
        String input2 = "%ASA-9-102021: Teardown ICMP connection for faddr 10.22.8.74/0(LOCAL\\user.name) gaddr 10.22.8.205/0 laddr 10.22.8.205/0";
        Message message2 = Message.builder()
                .addField(FieldName.of("original_string"), FieldValue.of(input2))
                .build();

        System.out.println("\n\n----> Parsing a message that should cause an error...");
        List<Message> results2 = runner.run(message2, chain);
        printResults(results2);
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
        // a csv parser is used to extract the 'asa_tag' for routing
        CSVParser csvParser = new CSVParser()
                .withInputField(FieldName.of("original_string"))
                .withDelimiter(Regex.of("\\s+"))
                .withOutputField(FieldName.of("asa_tag"), 0);

        // timestamps the message with 'processing_time'
        TimestampParser timestampParser = new TimestampParser()
                .withOutputField(FieldName.of("processing_time"));

        // will flag when an unexpected 'asa_tag' is encountered
        AlwaysFailParser alwaysFailParser = new AlwaysFailParser()
                .withError("Unexpected 'asa_tag'");

        // CSV -> Router -> Timestamp
        return new ChainBuilder()
                .then(csvParser)
                .routeBy(FieldName.of("asa_tag"))
                .then(Regex.of("%ASA-6-302021:"), timestampParser)
                .then(Regex.of("%ASA-9-102021:"), alwaysFailParser)
                .head();
    }
}
