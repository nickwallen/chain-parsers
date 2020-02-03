package com.cloudera.ccp.chains.parsers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ClassIndexParserCatalogTest {

    @MessageParser(name="Fake Parser", description="Parser created for catalog tests.")
    private static class FakeParser implements Parser {
        @Override
        public Message parse(Message message) {
            // do nothing
            return null;
        }

        @Override
        public List<FieldName> outputFields() {
            // do nothing
            return null;
        }

        @Override
        public List<ConfigName> validConfigurations() {
            // do nothing
            return null;
        }

        @Override
        public void configure(ConfigName configName, List<ConfigValue> configValues) {
            // do nothing
        }
    }

    @Test
    void findParser() {
        ParserCatalog catalog = new ClassIndexParserCatalog();
        List<ParserInfo> parsers = catalog.getParsers();
        boolean foundFakeParser = false;
        for(ParserInfo parserInfo: parsers) {
            if(FakeParser.class.equals(parserInfo.getParserClass())) {
                // found the fake parser
                foundFakeParser = true;
                assertEquals("Fake Parser", parserInfo.getName());
                assertEquals("Parser created for catalog tests.", parserInfo.getDescription());
            }
        }
        assertTrue(foundFakeParser);
    }

    /**
     * A parser that does not implement {@link Parser}.  This is not a valid parser.
     */
    @MessageParser(name="Bad Parser", description="A description.")
    private static class BadParser { }

    @Test
    void badParser() {
        ParserCatalog catalog = new ClassIndexParserCatalog();
        List<ParserInfo> parsers = catalog.getParsers();
        for(ParserInfo parserInfo: parsers) {
            if(BadParser.class.equals(parserInfo.getParserClass())) {
                fail("Should not have 'found' this bad parser.");
            }
        }
    }
}
