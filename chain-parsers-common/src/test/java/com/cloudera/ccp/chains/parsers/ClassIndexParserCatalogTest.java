package com.cloudera.ccp.chains.parsers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void testCatalog() {
        ParserCatalog catalog = new ClassIndexParserCatalog();
        List<MessageParser> annotations = catalog.getParsers();
        assertTrue(annotations.size() > 0);

        List<String> names = annotations.stream().map(a -> a.name()).collect(Collectors.toList());
        assertThat(names, hasItem("Fake Parser"));

        List<String> descriptions = annotations.stream().map(a -> a.description()).collect(Collectors.toList());
        assertThat(descriptions, hasItem("Parser created for catalog tests."));
    }
}
