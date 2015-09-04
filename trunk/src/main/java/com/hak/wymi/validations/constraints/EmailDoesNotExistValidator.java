package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import com.hak.wymi.validations.EmailDoesNotExist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class EmailDoesNotExistValidator implements ConstraintValidator<EmailDoesNotExist, Object> {
    @Autowired
    private UserDao userDao;

    @Override
    public void initialize(EmailDoesNotExist emailDoesNotExist) {
        // Does not take any setup.
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext cxt) {
        if (object != null) {
            String email = null;
            if (object instanceof User) {
                email = ((User) object).getEmail();
            } else if (object instanceof String) {
                email = (String) object;
            }
            if (email != null && !"".equals(email)) {
                return userDao.getFromEmail(email) == null;
            }
        }

        return false;
    }
}