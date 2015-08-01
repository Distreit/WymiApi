package com.hak.wymi.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ControllerAdvice
public class RestErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public HashMap<String, Object> processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        HashMap<String, Object> values = new HashMap<>();
        List<String> errorList = new ArrayList<>();
        values.put("errors", errorList);

        for (FieldError error : result.getFieldErrors()) {
            errorList.add(error.getDefaultMessage());
        }

        for (ObjectError error : result.getGlobalErrors()) {
            errorList.add(error.getDefaultMessage());
        }

        return values;
    }
}