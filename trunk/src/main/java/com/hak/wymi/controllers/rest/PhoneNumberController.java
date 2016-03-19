package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/phone-number")
public class PhoneNumberController {

    @Autowired
    private UserManager userManager;

    @RequestMapping(value = "/{phoneNumber}", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> phoneNumberExists(@PathVariable String phoneNumber) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        if (phoneNumber != null && !phoneNumber.equals("") && userManager.phoneNumberExists(phoneNumber)) {
            status = HttpStatus.NO_CONTENT;
        }

        return new ResponseEntity<>(new UniversalResponse(), status);
    }
}
