package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/email")
public class EmailController {

    @Autowired
    private UserManager userManager;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> emailExists(@RequestParam String email) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        if (email != null && !email.equals("") && userManager.emailExists(email)) {
            status = HttpStatus.NO_CONTENT;
        }

        return new ResponseEntity<>(new UniversalResponse(), status);
    }
}
