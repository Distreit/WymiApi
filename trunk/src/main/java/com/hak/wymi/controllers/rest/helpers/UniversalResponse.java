package com.hak.wymi.controllers.rest.helpers;

import com.fasterxml.jackson.annotation.JsonValue;
import com.hak.wymi.persistance.pojos.unsecure.interfaces.SecureToSend;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UniversalResponse {
    private static final String DATA = "data";
    private static final String ERRORS = "errors";

    private final ConcurrentMap<String, Object> data;

    public UniversalResponse() {
        this.data = new ConcurrentHashMap<>(2);
    }

    public UniversalResponse setData(SecureToSend secureToSend) {
        this.data.put(DATA, secureToSend);
        return this;
    }

    public UniversalResponse addUnknownError() {
        this.addError(new ResponseError("Unknown error"));
        return this;
    }

    public UniversalResponse addError(ResponseError responseError) {
        if (!this.data.containsKey(ERRORS)) {
            this.data.put(ERRORS, new ErrorList());
        }
        ((ErrorList) this.data.get(ERRORS)).add(responseError);
        return this;
    }

    @JsonValue
    public ConcurrentMap<String, Object> getData() {
        return data;
    }

    public UniversalResponse setData(List<SecureToSend> secureToSend) {
        this.data.put(DATA, secureToSend);
        return this;
    }
}
