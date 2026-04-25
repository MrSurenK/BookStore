package com.example.bookstore.validator;

import com.example.bookstore.utility.ValidYear;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class YearValidator implements ConstraintValidator<ValidYear, Integer> {


    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if(value == null) return true;

        int currentYear = Year.now().getValue();
        return value <= currentYear;
    }
}
