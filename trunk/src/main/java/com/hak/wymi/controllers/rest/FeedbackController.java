package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.EmailManager;
import com.hak.wymi.persistance.pojos.email.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(value = "/feedback")
public class FeedbackController {


    @Autowired
    private EmailManager emailManager;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> emailExists(@RequestBody String text, Principal principal) {
        final String message;
        if (principal != null) {
            message = String.format("Username: %s\nComment:\n\n%s", principal.getName(), text);
        } else {
            message = String.format("Username: ''\nComment:\n\n%s", text);
        }
        final Email email = new Email("niprat@gmail.com", "Feedback", message);
        emailManager.save(email);

        return new ResponseEntity<>(new UniversalResponse(), HttpStatus.ACCEPTED);
    }
}
