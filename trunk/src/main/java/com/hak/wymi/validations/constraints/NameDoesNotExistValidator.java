package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.managers.TopicManager;
import com.hak.wymi.persistance.managers.UserManager;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.validations.NameDoesNotExist;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameDoesNotExistValidator implements ConstraintValidator<NameDoesNotExist, Object> {
    @Autowired
    private UserManager userManager;

    @Autowired
    private TopicManager topicManager;

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
                    return userManager.getFromName(name) == null;
                }
            } else if (object instanceof Topic) {
                name = ((Topic) object).getName();
                if (name != null && !"".equals(name)) {
                    return topicManager.get(name) == null;
                }
            }
        }

        return false;
    }
}