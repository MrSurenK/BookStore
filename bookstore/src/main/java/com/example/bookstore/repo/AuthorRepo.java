package com.example.bookstore.repo;

import com.example.bookstore.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AuthorRepo extends JpaRepository<Author, Long> {

    Optional<Author> findByNameAndBirthday(String name, LocalDate birthday);


}
