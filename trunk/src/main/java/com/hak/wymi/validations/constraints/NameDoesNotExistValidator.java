package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
import com.hak.wymi.validations.NameDoesNotExist;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameDoesNotExistValidator implements ConstraintValidator<NameDoesNotExist, Object> {
    @Autowired
    private UserDao userDao;

    @Override
    public void initialize(NameDoesNotExist NameDoesNotExist) {
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext cxt) {
        if (object != null) {
            String name = null;
            if (object instanceof User) {
                name = ((User) object).getName();
            } else if (object instanceof String) {
                name = (String) object;
            }
            if (name != null && !name.equals("")) {
                return userDao.getFromName(name) == null;
            }
        }

        return false;
    }
}