package com.example.bookstore.dto;

import java.time.LocalDate;

public record AuthorResDTO(
           String name,
    LocalDate birthday
) {}

