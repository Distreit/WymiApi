package com.hak.wymi.validations;

import com.hak.wymi.validations.constraints.EmailsMatchValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EmailsMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailsMatch {
    String message() default "Emails do not match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}