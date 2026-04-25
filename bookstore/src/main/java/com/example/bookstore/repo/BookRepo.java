package com.example.bookstore.repo;

import com.example.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepo extends JpaRepository<Book, String > {


    Optional<Book> findBookByIdAndIsDeletedFalse(String isbn); //only want book that are not deleted


    @Query("""
            SELECT DISTINCT b FROM Book b 
            LEFT JOIN b.authors a 
            WHERE 
                (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
                OR 
                (:authorNames IS NULL OR LOWER(a.name) IN :authorNames) 
            """)
    List<Book> searchBooks(@Param("title")String title,
                                         @Param("authorNames") List<String> authorName);




}
