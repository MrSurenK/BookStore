package com.example.bookstore.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; //User need to add author if author does not exist in database

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthday;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books; //User will first initialize empty book for new author and add author in Book table
}
