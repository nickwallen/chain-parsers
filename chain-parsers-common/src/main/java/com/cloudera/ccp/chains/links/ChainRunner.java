package com.cloudera.ccp.chains.links;

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

        Optional<ChainLink> nextLink = Optional.of(chain);
        do {
            // parse the message
            Message input = results.get(results.size()-1);
            Parser parser = nextLink.get().getParser();
            Message output = parser.parse(input);
            results.add(output);

            // get the next link in the chain
            nextLink = nextLink.get().getNext(output);

            // if there is an error, stop parsing the message
            if(output.getError().isPresent()) {
                break;
            }

        } while(nextLink.isPresent());

        return results;
    }
}
