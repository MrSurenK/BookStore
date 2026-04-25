package com.example.bookstore.dto;

import java.util.List;

public record BookResponseDTO(
        String isbn,
        String title,
        List<AuthorResDTO> authors,
        int year,
        double price,
        String genre,
        boolean deleted
) {
}
