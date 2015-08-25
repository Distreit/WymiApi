package com.hak.wymi.validations.constraints;

import com.hak.wymi.persistance.pojos.unsecure.post.Post;
import com.hak.wymi.persistance.pojos.unsecure.post.PostDao;
import com.hak.wymi.validations.Exists;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistsValidator implements ConstraintValidator<Exists, Object> {
    @Autowired
    private PostDao postDao;

    @Override
    public void initialize(Exists exists) {
        // Does not take any setup.
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return o instanceof Post && postDao.get(((Post) o).getPostId()) != null;

    }

}
