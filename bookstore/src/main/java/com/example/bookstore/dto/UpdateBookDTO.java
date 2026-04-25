package com.example.bookstore.dto;

import com.example.bookstore.utility.ValidYear;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Set;

public record UpdateBookDTO(

        @NotNull(message = "Please provide the isbn of the book you want to update")
        String isbn,

        String title,

        @Valid
        Set<AuthorIdentifierDTO> authors,//need to give author name and birthday to find the right author

        @Min(value = 1000, message = "Year must be valid")
        @ValidYear
        Integer year, //validate yaer

        @Positive(message = "Please provide a valid price")
        Double price,

        String genre
) {
}
