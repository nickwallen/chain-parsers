package com.cloudera.ccp.chains.parsers;

import com.cloudera.ccp.chains.ChainRunner;
import org.atteo.classindex.ClassIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ParserCatalog} that builds a catalog parsers using a class index
 * compiled at build time.
 *
 * https://github.com/atteo/classindex
 */
public class ClassIndexParserCatalog implements ParserCatalog {
    Logger logger = LoggerFactory.getLogger(ChainRunner.class);

    @Override
    public List<ParserInfo> getParsers() {
        List<ParserInfo> results = new ArrayList<>();

        // search the class index for the annotation
        Iterable<Class<?>> knownAnnotations = ClassIndex.getAnnotated(MessageParser.class);
        for(Class<?> clazz: knownAnnotations) {
            MessageParser annotation = clazz.getAnnotation(MessageParser.class);
            if(Parser.class.isAssignableFrom(clazz)) {
                // found a parser
                Class<Parser> parserClass = (Class<Parser>) clazz;
                results.add(ParserInfo.builder()
                        .withName(annotation.name())
                        .withDescription(annotation.description())
                        .withParserClass(parserClass)
                        .build());
            } else {
                logger.warn("Found class with annotation '{}', but does not implement '{}'; class={}",
                        MessageParser.class.getName(), Parser.class.getName(), clazz.getName());
            }
        }

        return results;
    }
}
