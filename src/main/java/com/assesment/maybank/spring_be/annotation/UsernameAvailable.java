package com.assesment.maybank.spring_be.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import com.assesment.maybank.spring_be.validator.UsernameAvailableValidator;

@Documented
@Constraint(validatedBy = UsernameAvailableValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UsernameAvailable {
    String message() default "Username already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
