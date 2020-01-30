package com.cloudera.ccp.chains.parsers;

import org.atteo.classindex.ClassIndex;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ParserCatalog} that builds a catalog parsers using a class index
 * compiled at build time.
 *
 * https://github.com/atteo/classindex
 */
public class ClassIndexParserCatalog implements ParserCatalog {

    @Override
    public List<MessageParser> getParsers() {
        List<MessageParser> parserAnnotations = new ArrayList<>();

        Iterable<Class<?>> knownParsers = ClassIndex.getAnnotated(MessageParser.class);
        for(Class<?> clazz: knownParsers) {
            MessageParser annotation = clazz.getAnnotation(MessageParser.class);
            parserAnnotations.add(annotation);
        }

        return parserAnnotations;
    }
}
