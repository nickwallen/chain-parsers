package com.cloudera.ccp.chains;

import com.cloudera.ccp.chains.links.ChainLink;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Parses a {@link Message} using a parser chain.
 */
public class ChainRunner {
    Logger logger = LoggerFactory.getLogger(ChainRunner.class);

    /**
     * Parses a message using a parser chain.
     * @param message The message to parse.
     * @param chain The parser chain that parses each message.
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
            logger.debug("Parsed message; parser={}, message={}", parser.getClass().getName(), message);

        } while(next.isPresent());

        return results;
    }
}
