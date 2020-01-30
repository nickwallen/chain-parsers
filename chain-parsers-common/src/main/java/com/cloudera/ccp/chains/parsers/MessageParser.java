package com.cloudera.ccp.chains.parsers;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A marker for {@link Parser} implementations.
 */
@Retention(RetentionPolicy.RUNTIME)
@IndexAnnotated
public @interface MessageParser {
    String name() default "";
    String description() default "";
}
