package com.example.bookstore.mapper;

import com.example.bookstore.dto.AuthorResDTO;
import com.example.bookstore.dto.BookResponseDTO;
import com.example.bookstore.model.Author;
import com.example.bookstore.model.Book;

import java.util.List;

public class BookMapper {

    public static BookResponseDTO toDTO(Book book) {
        return new BookResponseDTO(
                book.getIsbn(),
                book.getTitle(),
                book.getAuthors()
                        .stream()
                        .map(BookMapper::toDTO)
                        .toList(),
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