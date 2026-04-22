package com.example.bookstore.validator;

import com.example.bookstore.utility.ValidISBN;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ISBNValidator implements ConstraintValidator<ValidISBN, String> {


    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if(isbn == null) return false;

        String cleaned = isbn.replaceAll("-", "");

        if(cleaned.length() == 13){
            return isValidISBN13(cleaned); //check if valid ISBN 13
        } else if (cleaned.length() == 10){
            return isValidISBN10(cleaned);
        }
        return false;
    }


    private boolean isValidISBN13(String digits){
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int num = digits.charAt(i) - '0';
            sum += (i % 2 == 0) ? num : num * 3;
        }
        int check = (10 - (sum % 10)) % 10;
        return check == (digits.charAt(12) - '0');
    }


    private boolean isValidISBN10(String digits){
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (digits.charAt(i) - '0') * (10 - i);
        }

        char last = digits.charAt(9);
        int check = (last == 'X') ? 10 : last - '0';

        sum += check;
        return sum % 11 == 0;
    }
}
