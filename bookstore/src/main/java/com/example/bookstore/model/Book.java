package com.example.bookstore.model;


import com.example.bookstore.utility.ValidISBN;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Book {

    @Id
    @NotNull
    @ValidISBN
    @Column(length = 17, nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @NotEmpty(message = "A book must have at least one author")
    @ManyToMany(fetch = FetchType.LAZY)
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

    @Builder.Default
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isDeleted = false; //Defaults to false
}
