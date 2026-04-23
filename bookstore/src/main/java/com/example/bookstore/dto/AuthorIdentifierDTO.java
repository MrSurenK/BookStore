package com.example.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AuthorIdentifierDTO(
        @NotBlank(message = "What is the author's name?")
        String name,
        @NotNull(message = "When was the author born?")
        LocalDate birthday
) {
}
