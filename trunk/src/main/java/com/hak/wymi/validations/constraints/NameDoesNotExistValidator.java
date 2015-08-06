package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.topic.TopicDao;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.persistance.pojos.user.UserDao;
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
    public void initialize(NameDoesNotExist NameDoesNotExist) {
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext cxt) {
        if (object != null) {
            String name;
            if (object instanceof User) {
                name = ((User) object).getName();
                if (name != null && !name.equals("")) {
                    return userDao.getFromName(name) == null;
                }
            } else if (object instanceof Topic) {
                name = ((Topic) object).getName();
                if (name != null && !name.equals("")) {
                    return topicDao.get(name) == null;
                }
            }
        }

        return false;
    }
}