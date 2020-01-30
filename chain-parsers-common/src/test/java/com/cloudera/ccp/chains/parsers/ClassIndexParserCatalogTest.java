package com.cloudera.ccp.chains.parsers;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClassIndexParserCatalogTest {

    @Test
    void testCatalog() {
        ParserCatalog catalog = new ClassIndexParserCatalog();
        List<MessageParser> annotations = catalog.getParsers();
        assertTrue(annotations.size() > 0);

        for(MessageParser annotation: annotations) {
            assertTrue(StringUtils.isNotBlank(annotation.name()));
            assertTrue(StringUtils.isNotBlank(annotation.description()));
        }

    }
}
