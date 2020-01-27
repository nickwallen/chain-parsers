package com.cloudera.ccp.chains;

import com.cloudera.ccp.chains.links.ChainLink;
import com.cloudera.ccp.chains.parsers.Message;
import com.cloudera.ccp.chains.parsers.Parser;

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
}
