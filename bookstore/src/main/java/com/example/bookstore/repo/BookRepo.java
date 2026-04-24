package com.example.bookstore.repo;

import com.example.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepo extends JpaRepository<Book, String > {


    Optional<Book> findBookByIdAndIsDeletedFalse(String isbn); //only want book that are not deleted





}
