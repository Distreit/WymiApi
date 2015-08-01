package com.hak.wymi.controllers.rest;

import com.hak.wymi.persistance.pojos.callbackcode.CallbackCode;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeDao;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeType;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.groups.Default;
import java.math.BigInteger;
import java.security.Principal;
import java.security.SecureRandom;

@RestController
public class UserController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
    @PreAuthorize("hasRole('ROLE_USER')")
    public User getUser(Principal principal) {
        if (principal != null && !principal.getName().equals("")) {
            return userDao.get(principal);
        } else {
            return null;
        }
    }

    @RequestMapping(
            value = "/user",
            method = RequestMethod.POST,
            produces = "application/json; charset=utf-8")
    public ResponseEntity<User> postUser(@Validated({Default.class, User.Registration.class}) @RequestBody User user) {
        user.setRoles("ROLE_USER");
        if (userDao.save(user)) {
            String code = getValidationCode(user);
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

    private String getValidationCode(User user) {
        CallbackCode callbackCode = new CallbackCode();
        callbackCode.setUser(user);
        callbackCode.setCode((new BigInteger(130, secureRandom)).toString(32));
        callbackCode.setType(CallbackCodeType.VALIDATION);
        callbackCodeDao.save(callbackCode);
        return callbackCode.getCode();
    }
}
