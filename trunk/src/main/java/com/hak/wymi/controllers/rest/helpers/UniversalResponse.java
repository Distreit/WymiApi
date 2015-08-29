package com.hak.wymi.controllers.rest.helpers;

import com.fasterxml.jackson.annotation.JsonValue;
import com.hak.wymi.persistance.pojos.unsecure.interfaces.SecureToSend;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UniversalResponse {
    private static final String DATA = "data";
    private static final String ERRORS = "errors";
    private static final String MESSAGES = "messages";
    private static final int INITIAL_SIZE = 2;

    private final ConcurrentMap<String, Object> entries;

    ErrorList errorList;
    LinkedList<String> messages;

    public UniversalResponse() {
        this.entries = new ConcurrentHashMap<>(INITIAL_SIZE);
    }

    @JsonValue
    public ConcurrentMap<String, Object> getData() {
        return entries;
    }

    public UniversalResponse setData(SecureToSend secureToSend) {
        this.entries.put(DATA, secureToSend);
        return this;
    }

    public UniversalResponse setData(List<SecureToSend> secureToSend) {
        this.entries.put(DATA, secureToSend);
        return this;
    }

    public UniversalResponse addUnknownError() {
        this.addError(new ResponseError("Unknown error"));
        return this;
    }

    public UniversalResponse addError(String errorMessage) {
        this.addError(new ResponseError(errorMessage));
        return this;
    }

    public UniversalResponse addError(ResponseError responseError) {
        if (!this.entries.containsKey(ERRORS)) {
            errorList = new ErrorList();
            this.entries.put(ERRORS, errorList);
        }
        errorList.add(responseError);
        return this;
    }

    public UniversalResponse addMessage(String message) {
        if (!this.entries.containsKey(MESSAGES)) {
            messages = new LinkedList<>();
            this.entries.put(MESSAGES, messages);
        }
        messages.add(message);
        return this;
    }
}
