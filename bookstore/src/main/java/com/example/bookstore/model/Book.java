package com.example.bookstore.model;


import com.example.bookstore.utility.ValidISBN;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Book {

    @Id
    @NotNull
    @ValidISBN
    @Column(length = 17, nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @NotEmpty(message = "A book must have at least one author")
    @ManyToMany
    @JoinTable(
            name="book_author",
            joinColumns = @JoinColumn(name="book_isbn"),
            inverseJoinColumns = @JoinColumn(name="author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private boolean isDeleted = false; //Defaults to false
}
