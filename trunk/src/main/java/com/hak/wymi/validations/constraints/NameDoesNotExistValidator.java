package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.pojos.unsecure.Topic;
import com.hak.wymi.persistance.pojos.unsecure.User;
import com.hak.wymi.persistance.pojos.unsecure.dao.TopicDao;
import com.hak.wymi.persistance.pojos.unsecure.dao.UserDao;
import com.hak.wymi.validations.NameDoesNotExist;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameDoesNotExistValidator implements ConstraintValidator<NameDoesNotExist, Object> {
    @Autowired
    private UserDao userDao;

    @Autowired
    private TopicDao topicDao;

    @Override
    public void initialize(NameDoesNotExist nameDoesNotExist) {
        // Does not take any setup.
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext cxt) {
        if (object != null) {
            String name;
            if (object instanceof User) {
                name = ((User) object).getName();
                if (name != null && !"".equals(name)) {
                    return userDao.getFromName(name) == null;
                }
            } else if (object instanceof Topic) {
                name = ((Topic) object).getName();
                if (name != null && !"".equals(name)) {
                    return topicDao.get(name) == null;
                }
            }
        }

        return false;
    }
}