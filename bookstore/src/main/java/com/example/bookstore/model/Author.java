package com.example.bookstore.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Entity
@Table(
        name="author",
        uniqueConstraints = @UniqueConstraint(name="uk_author_name_birthday", columnNames = {"name", "birthday"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id; //User need to add author if author does not exist in database

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthday;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>(); //Since using builder pattern initialise empty hashset first to prevent errors
    //User will first initialize empty book for new author and add author in Book table
}