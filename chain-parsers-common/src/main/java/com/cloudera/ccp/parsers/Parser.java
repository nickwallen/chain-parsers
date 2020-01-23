package com.cloudera.ccp.parsers;

import java.util.List;

public interface Parser {

    ParserResult parse(Message message);

    /**
     * TODO some parsers have a known, static set of output fields.  Others do not.
     */
    List<FieldName> outputFields();
}
