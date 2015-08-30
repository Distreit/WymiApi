package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.ResponseError;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
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

@ControllerAdvice
public class RestErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestErrorHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public UniversalResponse processValidationError(MethodArgumentNotValidException exception) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final BindingResult result = exception.getBindingResult();

        result.getGlobalErrors().stream().map(ResponseError::new).forEach(universalResponse::addError);
        result.getFieldErrors().stream().map(ResponseError::new).forEach(universalResponse::addError);

        return universalResponse;
    }

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public UniversalResponse processValidationError(Exception exception) {
        return new UniversalResponse().addError(exception.getMessage());
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public UniversalResponse processAccessDenied(Exception exception) {
        return new UniversalResponse().addError(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public UniversalResponse processException(Exception exception) {
        LOGGER.error(ExceptionUtils.getStackTrace(exception));
        return new UniversalResponse().addUnknownError();
    }
}