package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.CallbackCodeManager;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(value = "/code")
public class CodeController {
    @Autowired
    private CallbackCodeManager callbackCodeManager;

    @Value("${site.domain}")
    private String siteDomain;

    @RequestMapping(value = "/{code}", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> registerNewUser(@PathVariable String code) throws InvalidValueException {
        final String response = callbackCodeManager.processCode(code);
        if (response != null) {
            return new ResponseEntity<>(new UniversalResponse().setData(response), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(new UniversalResponse(), HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{type}", method = RequestMethod.POST, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> createNewCode(@PathVariable String type, Principal principal) {
        callbackCodeManager.generate(type, principal);
        return new ResponseEntity<>(new UniversalResponse(), HttpStatus.ACCEPTED);
    }
}
