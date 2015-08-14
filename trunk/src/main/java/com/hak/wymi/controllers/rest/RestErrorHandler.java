package com.hak.wymi.controllers.rest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestErrorHandler {
    protected static final Logger logger = LoggerFactory.getLogger(RestErrorHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public HashMap<String, Object> processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        HashMap<String, Object> values = new HashMap<>();
        List<String> errorList = new ArrayList<>();
        values.put("errors", errorList);

        errorList.addAll(result.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList()));

        errorList.addAll(result.getGlobalErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList()));

        return values;
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public HashMap<String, Object> processValidationError(ValidationException ex) {
        HashMap<String, Object> values = new HashMap<>();

        List<String> errorList = new ArrayList<>();
        values.put("errors", errorList);

        errorList.add(ex.getMessage());

        return values;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public HashMap<String, Object> processAuthenticationError(AccessDeniedException ex) {
        HashMap<String, Object> values = new HashMap<>();

        List<String> errorList = new ArrayList<>();
        values.put("errors", errorList);

        errorList.add(ex.getMessage());

        return values;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public HashMap<String, Object> processException(Exception ex) {
        logger.error(ExceptionUtils.getStackTrace(ex));
        HashMap<String, Object> values = new HashMap<>();

        List<String> errorList = new ArrayList<>();
        values.put("errors", errorList);

        errorList.add("Unhandled exception.");

        return values;
    }
}