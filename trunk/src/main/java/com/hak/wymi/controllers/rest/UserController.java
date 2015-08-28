package com.hak.wymi.controllers.rest;

import com.hak.wymi.persistance.pojos.PasswordChange;
import com.hak.wymi.persistance.pojos.unsecure.CallbackCode;
import com.hak.wymi.persistance.pojos.unsecure.CallbackCodeType;
import com.hak.wymi.persistance.pojos.unsecure.User;
import com.hak.wymi.persistance.pojos.unsecure.dao.CallbackCodeDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.UserDao;
import com.hak.wymi.utility.AppConfig;
import com.hak.wymi.validations.groups.Creation;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
public class UserController {
    // I think, but I'm not sure, this is the number of characters that the output will have.
    private static final int NUMBER_OF_CHARACTERS = 130;

    // Not sure about this either. Mostly magic.
    private static final int RADIX = 32;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CallbackCodeDao callbackCodeDao;

    @Autowired
    private SecureRandom secureRandom;

    @Autowired
    private JavaMailSender mailSender;

    @RequestMapping(
            value = "/user",
            method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ConcurrentMap<String, String> getUser(Principal principal) {
        if (principal != null && !"".equals(principal.getName())) {
            final User user = userDao.get(principal);

            final ConcurrentMap<String, String> result = new ConcurrentHashMap<>();
            result.put("name", user.getName());
            result.put("email", user.getEmail());
            result.put("validated", user.getValidated().toString());
            return result;
        }
        return null;
    }

    @RequestMapping(
            value = "/user/name/{userName}/password-reset",
            method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public ResponseEntity<String> getSendPasswordReset(@PathVariable String userName) {

        final User user = userDao.getFromName(userName);
        if (user != null) {
            final String code = getValidationCode(user, CallbackCodeType.PASSWORD_RESET);

            final SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("WYMI password reset request");
            message.setText(
                    String.format(
                            "Please click here to reset your password: http://%s/wymi/password-reset?code=%s",
                            AppConfig.get("IP"),
                            code
                    )
            );

            mailSender.send(message);

            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(
            value = "/user/password",
            method = RequestMethod.PUT,
            produces = "application/json; charset=utf-8")
    public ResponseEntity<String> getSendPasswordChange(@Valid @RequestBody PasswordChange passwordChange) {
        final CallbackCode callbackCode = callbackCodeDao.getFromCode(passwordChange.getCode(), CallbackCodeType.PASSWORD_RESET);

        if (callbackCode != null) {
            final User user = callbackCode.getUser();
            user.setPassword(DigestUtils.sha256Hex(passwordChange.getPassword()));
            if (userDao.update(user)) {
                callbackCodeDao.delete(callbackCode);
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            }
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(
            value = "/user",
            method = RequestMethod.POST,
            produces = "application/json; charset=utf-8")
    public ResponseEntity<User> registerNewUser(@Validated({Default.class, Creation.class}) @RequestBody User user) {
        user.setRoles("ROLE_USER");
        user.setPassword(DigestUtils.sha256Hex(user.getPassword()));
        if (userDao.save(user)) {
            final String code = getValidationCode(user, CallbackCodeType.VALIDATION);
            final SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("WYMI account validation");
            message.setText(String.format("Please click here to validate your account: http://%s/wymi/api/user/%s/validate/%s", AppConfig.get("IP"), user.getName(), code));
            mailSender.send(message);

            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getValidationCode(User user, CallbackCodeType type) {
        final CallbackCode callbackCode = new CallbackCode();
        callbackCode.setUser(user);
        callbackCode.setCode((new BigInteger(NUMBER_OF_CHARACTERS, secureRandom)).toString(RADIX));
        callbackCode.setType(type);
        callbackCodeDao.save(callbackCode);
        return callbackCode.getCode();
    }
}
