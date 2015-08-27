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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestErrorHandler.class);

    private static final String ERRORS = "errors";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> processValidationError(MethodArgumentNotValidException exception) {
        final BindingResult result = exception.getBindingResult();
        final Map<String, Object> values = new ConcurrentHashMap<>();
        final List<String> errorList = new ArrayList<>();
        values.put(ERRORS, errorList);

        errorList.addAll(result.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList()));

        errorList.addAll(result.getGlobalErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList()));

        return values;
    }

    @ExceptionHandler({ValidationException.class, IllegalArgumentException.class, AccessDeniedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> processValidationError(Exception exception) {
        final Map<String, Object> values = new ConcurrentHashMap<>();

        final List<String> errorList = new ArrayList<>();
        values.put(ERRORS, errorList);

        errorList.add(exception.getMessage());

        return values;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> processException(Exception exception) {
        LOGGER.error(ExceptionUtils.getStackTrace(exception));
        final Map<String, Object> values = new ConcurrentHashMap<>();

        final List<String> errorList = new ArrayList<>();
        values.put(ERRORS, errorList);

        errorList.add("Unhandled exception.");

        return values;
    }
}