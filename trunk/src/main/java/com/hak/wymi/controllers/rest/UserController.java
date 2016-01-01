package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.BalanceManager;
import com.hak.wymi.persistance.managers.CallbackCodeManager;
import com.hak.wymi.persistance.managers.EmailManager;
import com.hak.wymi.persistance.managers.UserManager;
import com.hak.wymi.persistance.pojos.PasswordChange;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCode;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeType;
import com.hak.wymi.persistance.pojos.email.Email;
import com.hak.wymi.persistance.pojos.user.SecureCurrentUser;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.utility.transactionprocessor.TransactionProcessor;
import com.hak.wymi.validations.groups.Creation;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.groups.Default;
import java.math.BigInteger;
import java.security.Principal;
import java.security.SecureRandom;

@RestController
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    // I think, but I'm not sure, this is the number of characters that the output will have.
    private static final int NUMBER_OF_CHARACTERS = 130;

    // Not sure about this either. Mostly magic.
    private static final int RADIX = 32;

    @Autowired
    private UserManager userManager;

    @Autowired
    private CallbackCodeManager callbackCodeManager;

    @Autowired
    private SecureRandom secureRandom;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Autowired
    private BalanceManager balanceManager;

    @Autowired
    private EmailManager emailManager;

    @Value("${site.domain}")
    private String siteDomain;

    @RequestMapping(value = "/user/current", method = RequestMethod.GET, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> getCurrentUser(Principal principal) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final SecureCurrentUser secureUser = userManager.getSecureCurrent(principal);

        universalResponse.addTransactions(secureUser.getUserId(), transactionProcessor, balanceManager);

        return new ResponseEntity<>(universalResponse.setData(secureUser), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/user/name/{userName}/password-reset", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getSendPasswordReset(@PathVariable String userName) {
        final UniversalResponse universalResponse = new UniversalResponse();

        try {
            final User user = userManager.getFromName(userName);
            final String code = getValidationCode(user, CallbackCodeType.PASSWORD_RESET);
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

    @RequestMapping(value = "/user/password", method = RequestMethod.PUT, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getSendPasswordChange(@Valid @RequestBody PasswordChange passwordChange) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final CallbackCode callbackCode = callbackCodeManager.getFromCode(passwordChange.getCode(), CallbackCodeType.PASSWORD_RESET);

        final User user = callbackCode.getUser();
        user.setPassword(DigestUtils.sha256Hex(passwordChange.getPassword()));
        userManager.update(user);
        callbackCodeManager.delete(callbackCode);
        return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> registerNewUser(@Validated({Default.class, Creation.class}) @RequestBody User user) {
        user.setRoles("ROLE_USER");
        user.setPassword(DigestUtils.sha256Hex(user.getPassword()));
        user.setWillingJuror(true);
        userManager.save(user);

        final String code = getValidationCode(user, CallbackCodeType.VALIDATION);
        final String body = String.format("Please click here to validate your account: http://%s/api/user/%s/validate/%s",
                siteDomain, user.getName(), code);
        final Email email = new Email(user.getEmail(), "WYMI account validation", body);
        emailManager.save(email);

        return new ResponseEntity<>(new UniversalResponse(), HttpStatus.CREATED);
    }

    private String getValidationCode(User user, CallbackCodeType type) {
        final CallbackCode callbackCode = new CallbackCode();
        callbackCode.setUser(user);
        callbackCode.setCode((new BigInteger(NUMBER_OF_CHARACTERS, secureRandom)).toString(RADIX));
        callbackCode.setType(type);
        callbackCodeManager.save(callbackCode);
        return callbackCode.getCode();
    }

    @RequestMapping(value = "/username/{username}", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> usernameExists(@PathVariable String username) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        if (username != null && !username.equals("") && userManager.getFromName(username) != null) {
            status = HttpStatus.NO_CONTENT;
        }

        return new ResponseEntity<>(new UniversalResponse(), status);
    }

    @RequestMapping(value = "/email/", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> emailExists(@RequestParam String email) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        if (email != null && !email.equals("") && userManager.getFromEmail(email) != null) {
            status = HttpStatus.NO_CONTENT;
        }

        return new ResponseEntity<>(new UniversalResponse(), status);
    }
}
