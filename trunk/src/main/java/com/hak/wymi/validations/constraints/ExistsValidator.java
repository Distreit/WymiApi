package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.managers.PostManger;
import com.hak.wymi.persistance.pojos.post.Post;
import com.hak.wymi.validations.Exists;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistsValidator implements ConstraintValidator<Exists, Object> {
    @Autowired
    private PostManger postManger;

    @Override
    public void initialize(Exists exists) {
        // Does not take any setup.
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        return object instanceof Post && postManger.get(((Post) object).getPostId()) != null;

    }

}
