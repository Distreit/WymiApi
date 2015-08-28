package com.hak.wymi.controllers.rest.helpers;

import java.util.LinkedList;
import java.util.List;

public class ErrorList {
    private final List<ResponseError> responseErrors = new LinkedList<>();

    public ErrorList() {
        // Allow no arguments.
    }

    public ErrorList(String message) {
        this.add(message);
    }

    public void add(ResponseError responseError) {
        this.responseErrors.add(responseError);
    }

    public final void add(String message) {
        responseErrors.add(new ResponseError(message));
    }

    public void addAll(List<ResponseError> newResponseErrors) {
        responseErrors.addAll(newResponseErrors);
    }

    public List<ResponseError> getResponseErrors() {
        return responseErrors;
    }
}
