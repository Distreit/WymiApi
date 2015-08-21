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

    private boolean shouldExist;
    private String stringType;

    @Override
    public void initialize(Exists exists) {
        this.shouldExist = exists.shouldExist();
        this.stringType = exists.stringType();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (o instanceof Post) {
            return postDao.get(((Post) o).getPostId()) != null;
        }

        return false;
    }

}
