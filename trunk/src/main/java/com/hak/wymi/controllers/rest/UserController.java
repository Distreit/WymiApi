package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.BalanceManager;
import com.hak.wymi.persistance.managers.CallbackCodeManager;
import com.hak.wymi.persistance.managers.UserManager;
import com.hak.wymi.persistance.pojos.PasswordChange;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCode;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeType;
import com.hak.wymi.persistance.pojos.user.SecureCurrentUser;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.utility.TransactionProcessor;
import com.hak.wymi.validations.groups.Creation;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.groups.Default;
import java.math.BigInteger;
import java.security.Principal;
import java.security.SecureRandom;

@RestController
@RequestMapping(value = "/user")
public class UserController {
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
    private JavaMailSender mailSender;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Autowired
    private BalanceManager balanceManager;

    @Value("${site.domain}")
    private String siteDomain;

    @RequestMapping(value = "/current", method = RequestMethod.GET, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> getCurrentUser(Principal principal) {
        final UniversalResponse universalResponse = new UniversalResponse();
        if (principal != null && !"".equals(principal.getName())) {
            final User user = userManager.get(principal);
            final SecureCurrentUser secureUser = new SecureCurrentUser(user, principal);

            universalResponse.addTransactions(principal, user, transactionProcessor, balanceManager);

            return new ResponseEntity<>(universalResponse.setData(secureUser), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/name/{userName}/password-reset", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getSendPasswordReset(@PathVariable String userName) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final User user = userManager.getFromName(userName);

        if (user != null) {
            final String code = getValidationCode(user, CallbackCodeType.PASSWORD_RESET);

            final SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("WYMI password reset request");
            message.setText(
                    String.format(
                            "Please click here to reset your password: http://%s/password-reset?code=%s",
                            siteDomain,
                            code
                    )
            );

            mailSender.send(message);

            return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/password", method = RequestMethod.PUT, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getSendPasswordChange(@Valid @RequestBody PasswordChange passwordChange) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final CallbackCode callbackCode = callbackCodeManager.getFromCode(passwordChange.getCode(), CallbackCodeType.PASSWORD_RESET);

        if (callbackCode != null) {
            final User user = callbackCode.getUser();
            user.setPassword(DigestUtils.sha256Hex(passwordChange.getPassword()));
            if (userManager.update(user)) {
                callbackCodeManager.delete(callbackCode);
                return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
            }
        }
        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> registerNewUser(@Validated({Default.class, Creation.class}) @RequestBody User user) {
        final UniversalResponse universalResponse = new UniversalResponse();
        user.setRoles("ROLE_USER");
        user.setPassword(DigestUtils.sha256Hex(user.getPassword()));
        if (userManager.save(user)) {
            final String code = getValidationCode(user, CallbackCodeType.VALIDATION);
            final SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("WYMI account validation");
            message.setText(String
                    .format("Please click here to validate your account: http://%s/api/user/%s/validate/%s",
                            siteDomain, user.getName(), code));
            mailSender.send(message);

            return new ResponseEntity<>(universalResponse, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getValidationCode(User user, CallbackCodeType type) {
        final CallbackCode callbackCode = new CallbackCode();
        callbackCode.setUser(user);
        callbackCode.setCode((new BigInteger(NUMBER_OF_CHARACTERS, secureRandom)).toString(RADIX));
        callbackCode.setType(type);
        callbackCodeManager.save(callbackCode);
        return callbackCode.getCode();
    }
}
