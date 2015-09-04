package com.hak.wymi.validations;

import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeType;
import com.hak.wymi.validations.constraints.CallbackCodeExistsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CallbackCodeExistsValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CallbackCodeExists {
    String message() default "Code does not exist";

    CallbackCodeType type();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}