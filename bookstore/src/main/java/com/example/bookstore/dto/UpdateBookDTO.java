package com.example.bookstore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Set;

public record UpdateBookDTO(

        @NotNull(message = "Please provide the isbn of the book you want to update")
        String isbn,

        String title,

        @Valid
        Set<AuthorIdentifierDTO> authors,//need to give author name and birthday to find the right author

        int year, //validate yaer

        @Positive(message = "Please provide a valid price")
        double price,

        String genre
) {
}
