package com.hak.wymi.controllers.rest.helpers;

import org.springframework.validation.ObjectError;

public class ResponseError {
    private String message;

    public ResponseError(String message) {
        this.message = message;
    }

    public ResponseError(ObjectError error) {
        this.message = error.getDefaultMessage();
    }

    public String getMessage() {
        return message;
    }
}
