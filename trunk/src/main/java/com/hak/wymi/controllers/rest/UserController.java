package com.hak.wymi.controllers.rest;

import com.hak.wymi.persistance.pojos.PasswordChange;
import com.hak.wymi.persistance.pojos.unsecure.callbackcode.CallbackCode;
import com.hak.wymi.persistance.pojos.unsecure.callbackcode.CallbackCodeDao;
import com.hak.wymi.persistance.pojos.unsecure.callbackcode.CallbackCodeType;
import com.hak.wymi.persistance.pojos.unsecure.user.User;
import com.hak.wymi.persistance.pojos.unsecure.user.UserDao;
import com.hak.wymi.validations.groups.Creation;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.groups.Default;
import java.math.BigInteger;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserDao userDao;

    @Autowired
    CallbackCodeDao callbackCodeDao;

    @Autowired
    private SecureRandom secureRandom;

    @Autowired
    private JavaMailSender mailSender;

    @RequestMapping(
            value = "/user",
            method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public Map<String, String> getUser(Principal principal) {
        if (principal != null && !"".equals(principal.getName())) {
            User user = userDao.get(principal);

            Map<String, String> result = new HashMap<>();
            result.put("name", user.getName());
            result.put("email", user.getEmail());
            result.put("validated", user.getValidated().toString());
            return result;
        } else {
            return null;
        }
    }

    @RequestMapping(
            value = "/user/name/{userName}/password-reset",
            method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public ResponseEntity<String> getSendPasswordReset(@PathVariable String userName) {

        User user = userDao.getFromName(userName);
        if (user != null) {
            String code = getValidationCode(user, CallbackCodeType.PASSWORD_RESET);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("WYMI password reset request");
            message.setText(
                    String.format(
                            "Please click here to reset your password: http://10.0.0.3/wymi/password-reset?code=%s",
                            code
                    )
            );

            mailSender.send(message);

            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/user/password",
            method = RequestMethod.PUT,
            produces = "application/json; charset=utf-8")
    public ResponseEntity<String> getSendPasswordChange(@Valid @RequestBody PasswordChange passwordChange) {
        CallbackCode callbackCode = callbackCodeDao.getFromCode(passwordChange.getCode(), CallbackCodeType.PASSWORD_RESET);

        if (callbackCode != null) {
            User user = callbackCode.getUser();
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
            String code = getValidationCode(user, CallbackCodeType.VALIDATION);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("WYMI account validation");
            message.setText(String.format("Please click here to validate your account: http://10.0.0.3/wymi/api/user/%s/validate/%s", user.getName(), code));
            mailSender.send(message);

            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getValidationCode(User user, CallbackCodeType type) {
        CallbackCode callbackCode = new CallbackCode();
        callbackCode.setUser(user);
        callbackCode.setCode((new BigInteger(130, secureRandom)).toString(32));
        callbackCode.setType(type);
        callbackCodeDao.save(callbackCode);
        return callbackCode.getCode();
    }
}
