package com.example.bookstore.utility;

import com.example.bookstore.dto.AuthorResDTO;
import com.example.bookstore.dto.BookResponseDTO;
import com.example.bookstore.model.Author;
import com.example.bookstore.model.Book;

import java.util.List;

public class BookMapper {

    public static BookResponseDTO toDTO(Book book) {
        List<AuthorResDTO> authors = book.getAuthors() == null ? List.of() : book.getAuthors()
                .stream()
                .map(BookMapper::toDTO)
                .toList();

        return new BookResponseDTO(
                book.getIsbn(),
                book.getTitle(),
                authors,
                book.getYear(),
                book.getPrice(),
                book.getGenre(),
                book.isDeleted()
        );
    }

    public static AuthorResDTO toDTO(Author author) {
        return new AuthorResDTO(
                author.getName(),
                author.getBirthday()
        );
    }
}