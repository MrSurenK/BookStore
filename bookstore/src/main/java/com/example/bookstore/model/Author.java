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
    private long id; //will manually create authors so no need to generate id

    @Column(nullable = false)
    private String name;

    private LocalDate birthday;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books;
}
