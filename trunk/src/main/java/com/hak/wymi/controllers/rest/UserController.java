package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.CallbackCodeManager;
import com.hak.wymi.persistance.managers.EmailManager;
import com.hak.wymi.persistance.managers.UserManager;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeType;
import com.hak.wymi.persistance.pojos.email.Email;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.groups.Creation;
import com.hak.wymi.validations.groups.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.groups.Default;
import java.security.Principal;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private CallbackCodeManager callbackCodeManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private EmailManager emailManager;

    @Value("${site.domain}")
    private String siteDomain;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> registerNewUser(@Validated({Default.class, Creation.class}) @RequestBody User user) {
        user.setRoles("ROLE_USER");
        user.setPassword(UserManager.calcPasswordHash(user.getPassword()));
        user.setReceivedFunds(false);
        userManager.save(user);

        return new ResponseEntity<>(new UniversalResponse(), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.PUT, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> updateUser(
            @PathVariable String userName,
            Principal principal,
            @Validated({Default.class, Update.class}) @RequestBody User user) {

        final UniversalResponse universalResponse = new UniversalResponse();

        if (userName.equalsIgnoreCase(principal.getName())) {
            userManager.applyChanges(user);
        }

        return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/{userName}/password-reset", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getSendPasswordReset(@PathVariable String userName) {
        final UniversalResponse universalResponse = new UniversalResponse();

        try {
            final User user = userManager.getFromName(userName);
            final String code = callbackCodeManager.saveNew(user, CallbackCodeType.PASSWORD_RESET);
            final String content = String.format("Please click here to reset your password: http://%s/password-reset?code=%s",
                    siteDomain, code);

            final Email email = new Email(user.getEmail(), "WYMI password reset request", content);
            emailManager.save(email);

        } catch (Exception e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ignore this, user not found when requesting password reset. " + userName, e);
            }
        }

        return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
    }
}
