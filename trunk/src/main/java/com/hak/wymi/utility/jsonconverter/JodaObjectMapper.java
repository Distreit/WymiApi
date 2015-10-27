package com.hak.wymi.utility.jsonconverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class JodaObjectMapper extends ObjectMapper {
    private static final long serialVersionUID = 7978224667742187395L;

    public JodaObjectMapper() {
        super();
        registerModule(new JodaModule());
    }
}
