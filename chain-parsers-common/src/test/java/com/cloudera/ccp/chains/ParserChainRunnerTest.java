package com.cloudera.ccp.chains;

import com.cloudera.ccp.chains.links.ChainBuilder;
import com.cloudera.ccp.chains.links.ChainLink;
import com.cloudera.ccp.chains.parsers.FieldName;
import com.cloudera.ccp.chains.parsers.FieldValue;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.core.AlwaysFailParser;
import com.cloudera.ccp.chains.parsers.core.TimestampParser;
import com.cloudera.ccp.chains.parsers.core.TimestampParserTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserChainRunnerTest {

    @Test
    void runOneLinkChain() {
        long now = System.currentTimeMillis();
        TimestampParser timestamp1 = new TimestampParser()
                .withClock(new TimestampParserTest.FixedClock(now))
                .withOutputField(FieldName.of("timestamp1"));

        // the parser chain: timestamp1
        ChainLink chain = new ChainBuilder()
                .then(timestamp1)
                .head();

        // run the parser chain
        Message input = Message.builder()
                .addField(FieldName.of("original_string"), FieldValue.of("some message"))
                .build();
        ParserChainRunner runner = new ParserChainRunner();
        List<Message> results = runner.run(input, chain);

        // the first message returned should include only the original input
        Message expected1 = Message.builder()
                .withFields(input)
                .build();
        assertEquals(expected1, results.get(0));

        // the second message should contain 'timestamp1'
        Message expected2 = Message.builder()
                .withFields(expected1)
                .addField(FieldName.of("timestamp1"), FieldValue.of(Long.toString(now)))
                .build();
        assertEquals(expected2, results.get(1));
    }

    @Test
    void runTwoLinkChain() {
        long now = System.currentTimeMillis();
        TimestampParser timestamp1 = new TimestampParser()
                .withClock(new TimestampParserTest.FixedClock(now))
                .withOutputField(FieldName.of("timestamp1"));

        TimestampParser timestamp2 = new TimestampParser()
                .withClock(new TimestampParserTest.FixedClock(now))
                .withOutputField(FieldName.of("timestamp2"));

        // the parser chain: timestamp1 -> timestamp2
        ChainLink chain = new ChainBuilder()
                .then(timestamp1)
                .then(timestamp2)
                .head();

        // run the parser chain
        Message input = Message.builder()
                .addField(FieldName.of("original_string"), FieldValue.of("some message"))
                .build();
        ParserChainRunner runner = new ParserChainRunner();
        List<Message> results = runner.run(input, chain);

        // the first message returned should include only the original input
        Message expected1 = Message.builder()
                .withFields(input)
                .build();
        assertEquals(expected1, results.get(0));

        // the second message should contain 'timestamp1'
        Message expected2 = Message.builder()
                .withFields(expected1)
                .addField(FieldName.of("timestamp1"), FieldValue.of(Long.toString(now)))
                .build();
        assertEquals(expected2, results.get(1));

        // the third message should also contain 'timestamp2'
        Message expected3 = Message.builder()
                .withFields(expected2)
                .addField(FieldName.of("timestamp2"), FieldValue.of(Long.toString(now)))
                .build();
        assertEquals(expected3, results.get(2));
    }

    @Test
    void runChainWithError() {
        long now = System.currentTimeMillis();
        TimestampParser timestamp1 = new TimestampParser()
                .withClock(new TimestampParserTest.FixedClock(now))
                .withOutputField(FieldName.of("timestamp1"));

        // the parser chain: timestamp1 -> timestamp2
        ChainLink chain = new ChainBuilder()
                .then(timestamp1)
                .then(new AlwaysFailParser())
                .head();

        // run the parser chain
        Message input = Message.builder()
                .addField(FieldName.of("original_string"), FieldValue.of("some message"))
                .build();
        ParserChainRunner runner = new ParserChainRunner();
        List<Message> results = runner.run(input, chain);

        // the first message returned should include only the original input
        Message expected1 = Message.builder()
                .withFields(input)
                .build();
        assertEquals(expected1, results.get(0));

        // the second message should contain 'timestamp1'
        Message expected2 = Message.builder()
                .withFields(expected1)
                .addField(FieldName.of("timestamp1"), FieldValue.of(Long.toString(now)))
                .build();
        assertEquals(expected2, results.get(1));

        // the last parser in the chain caused an error
        assertTrue(results.get(2).getError().isPresent());
    }
}
