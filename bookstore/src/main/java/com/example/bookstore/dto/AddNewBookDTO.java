package com.example.bookstore.dto;

import com.example.bookstore.model.Author;
import com.example.bookstore.utility.ValidISBN;
import com.example.bookstore.utility.ValidYear;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Map;
import java.util.Set;


public record AddNewBookDTO(

        @NotBlank(message = "Please provide the ISBN number of the book")
        String isbn,

        @NotBlank(message = "What is the title of this book")
        String title,

        @NotEmpty(message = "Please provide information about the author(s) of the book")
        @Valid
        Set<AuthorIdentifierDTO> authors,//need to give author name and birthday to find the right author

        @NotNull(message = "When was the book published?")
        @Min(value = 1000, message="Year must be valid")
        @ValidYear
        int year,

        @NotNull(message = "How much is this book?")
        @Positive(message = "Please provide a valid price")
        double price,

        @NotBlank(message = "What genre does this book belong to?")
        String genre
) { }
