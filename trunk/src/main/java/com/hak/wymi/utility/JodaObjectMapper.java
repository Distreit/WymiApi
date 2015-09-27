package com.hak.wymi.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class JodaObjectMapper extends ObjectMapper {
    public JodaObjectMapper() {
        super();
        registerModule(new JodaModule());
    }
}
