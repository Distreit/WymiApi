package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.CallbackCodeManager;
import com.hak.wymi.persistance.managers.EmailManager;
import com.hak.wymi.persistance.managers.UserManager;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeType;
import com.hak.wymi.persistance.pojos.email.Email;
import com.hak.wymi.persistance.pojos.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/email")
public class EmailController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private UserManager userManager;

    @Autowired
    private EmailManager emailManager;

    @Autowired
    private CallbackCodeManager callbackCodeManager;

    @Value("${site.domain}")
    private String siteDomain;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> emailExists(@RequestParam String email) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        if (email != null && !email.equals("") && userManager.emailExists(email)) {
            status = HttpStatus.NO_CONTENT;
        }

        return new ResponseEntity<>(new UniversalResponse(), status);
    }

    @RequestMapping(value = "/{email}/password-reset", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getSendPasswordReset(@PathVariable String email) {
        final UniversalResponse universalResponse = new UniversalResponse();

        try {
            final User user = userManager.getFromEmail(email);
            final String code = callbackCodeManager.saveNew(user, CallbackCodeType.PASSWORD_RESET);
            final String content = String.format("Please click here to reset your password: http://%s/password-reset?code=%s",
                    siteDomain, code);

            final Email actualEmail = new Email(user.getEmail(), "WYMI password reset request", content);
            emailManager.save(actualEmail);

        } catch (Exception e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ignore this, user not found when requesting password reset. " + email, e);
            }
        }

        return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
    }
}
