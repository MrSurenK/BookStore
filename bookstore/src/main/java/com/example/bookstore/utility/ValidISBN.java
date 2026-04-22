package com.example.bookstore.utility;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import com.example.bookstore.validator.ISBNValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ISBNValidator.class)
public @interface ValidISBN {

    String message() default "Invalid ISBN";

    Class<?> [] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
