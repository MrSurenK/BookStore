package com.example.bookstore.repo;

import com.example.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepo extends JpaRepository<Book, String > {
    Optional<Book> findBookByIsbnAndIsDeletedFalse(String isbn); //only want book that are not deleted


    //Gets the book if either the title or the author is specified and if both are specified it will get that book only
    @Query("""
    SELECT DISTINCT b FROM Book b
    JOIN b.authors a
    WHERE b.isDeleted = false
    AND (
        (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
    )
    AND (
        :authorNames IS NULL OR LOWER(a.name) IN :authorNames
    )
    """)
    List<Book> searchBooks(@Param("title")String title,
                                         @Param("authorNames") List<String> authorName);


    @Query("""
            SELECT b FROM Book b WHERE b.isDeleted=false
            """)
    List<Book> getAllNonDeletedBooks();

}
