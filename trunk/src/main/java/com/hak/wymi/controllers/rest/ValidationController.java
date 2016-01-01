package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.validations.constraints.UrlValidatorConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/validation")
public class ValidationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationController.class);

    @RequestMapping(value = "/url/", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> emailExists(@RequestParam String value) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        if (value != null && !value.equals("") && UrlValidatorConstraint.isValidUrl(value)) {
            status = HttpStatus.NO_CONTENT;
        }

        return new ResponseEntity<>(new UniversalResponse(), status);
    }
}
