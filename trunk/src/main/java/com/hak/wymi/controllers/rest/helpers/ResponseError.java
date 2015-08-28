package com.hak.wymi.controllers.rest.helpers;

import org.springframework.context.MessageSourceResolvable;

public class ResponseError {
    private final String message;

    public ResponseError(String message) {
        this.message = message;
    }

    public ResponseError(MessageSourceResolvable error) {
        this.message = error.getDefaultMessage();
    }

    public String getMessage() {
        return message;
    }
}
