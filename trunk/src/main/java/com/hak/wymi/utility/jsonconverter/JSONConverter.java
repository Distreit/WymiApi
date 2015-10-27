package com.hak.wymi.utility.jsonconverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JSONConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONConverter.class);
    private static JodaObjectMapper mapper = new JodaObjectMapper();

    private JSONConverter() {
        // Static only.
    }

    public static String getJSON(Object object, Boolean prettyPrint) {
        String result = null;
        try {
            if (prettyPrint) {
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
            } else {
                mapper.disable(SerializationFeature.INDENT_OUTPUT);
            }

            result = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to convert object to json", e);
        }

        return result;
    }
}
