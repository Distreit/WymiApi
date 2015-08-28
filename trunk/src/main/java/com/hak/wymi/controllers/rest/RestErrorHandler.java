package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.ErrorList;
import com.hak.wymi.controllers.rest.helpers.ResponseError;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.xml.bind.ValidationException;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestErrorHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorList processValidationError(MethodArgumentNotValidException exception) {
        final BindingResult result = exception.getBindingResult();
        final ErrorList errorList = new ErrorList();

        errorList.addAll(result.getFieldErrors().stream().map((f) -> new ResponseError(f.getDefaultMessage())).collect(Collectors.toList()));
        errorList.addAll(result.getGlobalErrors().stream().map((f) -> new ResponseError(f.getDefaultMessage())).collect(Collectors.toList()));

        return errorList;
    }

    @ExceptionHandler({ValidationException.class, AccessDeniedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorList processValidationError(Exception exception) {
        LOGGER.error(ExceptionUtils.getStackTrace(exception));
        return new ErrorList(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorList processException(Exception exception) {
        LOGGER.error(ExceptionUtils.getStackTrace(exception));
        return new ErrorList("Unhandled exception.");
    }
}