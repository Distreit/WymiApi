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
@RequestMapping(value = "/username")
public class UsernameController {

    @Autowired
    private UserManager userManager;

    @RequestMapping(value = "/{username}", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> usernameExists(@PathVariable String username) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        if (username != null && !username.equals("") && userManager.getFromName(username) != null) {
            status = HttpStatus.NO_CONTENT;
        }

        return new ResponseEntity<>(new UniversalResponse(), status);
    }
}
