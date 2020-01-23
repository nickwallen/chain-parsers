package com.cloudera.ccp.parsers;

import java.util.Collections;
import java.util.List;

public interface Parser {

    Message parse(Message message);

    /**
     * TODO some parsers have a known, static set of output fields.  Others do not.
     *
     * TODO should this be static or what?
     */
    List<FieldName> outputFields();
}
