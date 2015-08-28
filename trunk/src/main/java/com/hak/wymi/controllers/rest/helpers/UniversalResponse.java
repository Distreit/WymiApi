package com.hak.wymi.controllers.rest.helpers;

import com.fasterxml.jackson.annotation.JsonValue;
import com.hak.wymi.persistance.pojos.unsecure.interfaces.SecureToSend;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UniversalResponse {
    private static final String DATA = "data";
    private static final String ERRORS = "errors";
    private static final int INITIAL_SIZE = 2;

    private final ConcurrentMap<String, Object> entries;

    public UniversalResponse() {
        this.entries = new ConcurrentHashMap<>(INITIAL_SIZE);
    }

    public UniversalResponse setData(SecureToSend secureToSend) {
        this.entries.put(DATA, secureToSend);
        return this;
    }

    public UniversalResponse addUnknownError() {
        this.addError(new ResponseError("Unknown error"));
        return this;
    }

    public UniversalResponse addError(ResponseError responseError) {
        if (!this.entries.containsKey(ERRORS)) {
            this.entries.put(ERRORS, new ErrorList());
        }
        ((ErrorList) this.entries.get(ERRORS)).add(responseError);
        return this;
    }

    public UniversalResponse addError(String errorMessage) {
        this.addError(new ResponseError(errorMessage));
        return this;
    }

    @JsonValue
    public ConcurrentMap<String, Object> getData() {
        return entries;
    }

    public UniversalResponse setData(List<SecureToSend> secureToSend) {
        this.entries.put(DATA, secureToSend);
        return this;
    }
}
