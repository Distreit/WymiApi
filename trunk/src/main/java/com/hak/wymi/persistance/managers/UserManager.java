package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeType;
import com.hak.wymi.persistance.pojos.email.Email;
import com.hak.wymi.persistance.pojos.user.SecureCurrentUser;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
public class UserManager {
    @Autowired
    private CallbackCodeManager callbackCodeManager;

    @Autowired
    private EmailManager emailManager;

    @Autowired
    private UserDao userDao;

    @Value("${site.domain}")
    private String siteDomain;

    public static String calcPasswordHash(String plainTextPassword) {
        return DigestUtils.sha256Hex(plainTextPassword);
    }

    @Transactional
    public User get(Principal principal) {
        return userDao.get(principal);
    }

    @Transactional
    public void save(User user) {
        userDao.save(user);
        sendValidationEmail(user);
    }

    @Transactional
    private void sendValidationEmail(User user) {
        final String code = callbackCodeManager.saveNew(user, CallbackCodeType.VALIDATION);
        final String body = String.format(
                "Please click here to validate your account: http://%s/api/user/%s/validate/%s",
                siteDomain, user.getName(),
                code
        );
        final Email email = new Email(user.getEmail(), "WYMI account validation", body);
        emailManager.save(email);
    }

    @Transactional
    public User getFromName(String userName) {
        return userDao.getFromName(userName);
    }

    @Transactional
    public void update(User user) {
        userDao.update(user);
    }

    @Transactional
    public User getFromEmail(String email) {
        return userDao.getFromEmail(email);
    }

    @Transactional
    public SecureCurrentUser getSecureCurrent(Principal principal) {
        return new SecureCurrentUser(userDao.getFromName(principal.getName()), principal);
    }

    @Transactional
    public void applyChanges(User partialUser) {
        final User user = userDao.getFromName(partialUser.getName());
        if (calcPasswordHash(partialUser.getCurrentPassword()).equals(user.getPassword())) {
            boolean changed = false;

            final String newPassword = partialUser.getPassword();
            if (newPassword != null) {
                changed = true;
                user.setPassword(calcPasswordHash(newPassword));
            }

            if (partialUser.getPhoneNumber() != null && !partialUser.getPhoneNumber().equals(user.getPhoneNumber())) {
                changed = true;
                user.setPhoneNumber(partialUser.getPhoneNumber());
            }

            if (partialUser.getWillingJuror() != null && user.getWillingJuror() != partialUser.getWillingJuror()) {
                changed = true;
                user.setWillingJuror(partialUser.getWillingJuror());
            }

            if (partialUser.getEmail() != null) {
                changed = true;
                user.setNewEmail(partialUser.getEmail());
                sendChangeEmailEmail(user);
            }

            if (changed) {
                userDao.update(user);
            }
        } else {
            throw new UnsupportedOperationException("Bad password.");
        }
    }

    private void sendChangeEmailEmail(User user) {
        final String message = "Please click here to verify your new email: http://%s/validate?code=%s";
        final String code = callbackCodeManager.saveNew(user, CallbackCodeType.EMAIL_CHANGE);
        final String body = String.format(message, siteDomain, code);
        final Email email = new Email(user.getEmail(), "WYMI email change verification", body);
        emailManager.save(email);
    }

    @Transactional
    public boolean emailExists(String email) {
        return userDao.getFromEmail(email, true) != null;
    }

    @Transactional
    public boolean phoneNumberExists(String phoneNumber) {
        return userDao.getFromPhoneNumber(phoneNumber) != null;
    }
}
