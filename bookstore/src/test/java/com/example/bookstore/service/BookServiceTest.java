package com.example.bookstore.service;

import com.example.bookstore.dto.AddNewBookDTO;
import com.example.bookstore.dto.AuthorIdentifierDTO;
import com.example.bookstore.model.Author;
import com.example.bookstore.model.Book;
import com.example.bookstore.repo.AuthorRepo;
import com.example.bookstore.repo.BookRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepo bookRepo;

    @Mock
    AuthorRepo authorRepo;

    @InjectMocks
    BookService bookService;

    @Test
    void addNewBook_whenAuthorExists_savesBook() throws Exception {
        // arrange
        String isbn = "9780306406157";
        String title = "Test Driven Development";
        String authorName = "Jane Doe";
        LocalDate bday = LocalDate.of(1980, 1, 1);

        Author authorEntity = Author.builder()
                .id(1L)
                .name(authorName)
                .birthday(bday)
                .books(Set.of())
                .build();

        when(authorRepo.findByNameAndBirthday(authorName, bday)).thenReturn(Optional.of(authorEntity));

        AddNewBookDTO dto = new AddNewBookDTO(isbn, title, Set.of(new AuthorIdentifierDTO(authorName, bday)), 2020, 25.5, "Tech");

        // act
        bookService.addNewBook(dto);

        // assert
        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepo, times(1)).save(captor.capture());
        Book saved = captor.getValue();

        // use reflection to read private fields from Book and Author
        String savedIsbn = (String) getField(saved, "isbn");
        String savedTitle = (String) getField(saved, "title");
        int savedYear = (int) getField(saved, "year");
        double savedPrice = (double) getField(saved, "price");
        String savedGenre = (String) getField(saved, "genre");

        Object authorsObj = getField(saved, "authors");
        assertNotNull(authorsObj);
        @SuppressWarnings("unchecked")
        Set<Author> savedAuthors = (Set<Author>) authorsObj;
        assertEquals(1, savedAuthors.size());
        Author savedAuthor = savedAuthors.iterator().next();
        String savedAuthorName = (String) getField(savedAuthor, "name");
        LocalDate savedAuthorBday = (LocalDate) getField(savedAuthor, "birthday");

        assertEquals(isbn, savedIsbn);
        assertEquals(title, savedTitle);
        assertEquals(2020, savedYear);
        assertEquals(25.5, savedPrice, 0.0001);
        assertEquals("Tech", savedGenre);
        assertEquals(authorName, savedAuthorName);
        assertEquals(bday, savedAuthorBday);
    }

    @Test
    void addNewBook_whenAuthorMissing_throwsEntityNotFoundException() {
        // arrange
        String isbn = "9780306406157";
        String title = "Some Book";
        String authorName = "Missing Author";
        LocalDate bday = LocalDate.of(1990, 5, 5);

        when(authorRepo.findByNameAndBirthday(authorName, bday)).thenReturn(Optional.empty());

        AddNewBookDTO dto = new AddNewBookDTO(isbn, title, Set.of(new AuthorIdentifierDTO(authorName, bday)), 2019, 10.0, "Fiction");

        // act & assert
        assertThrows(EntityNotFoundException.class, () -> bookService.addNewBook(dto));
        verify(bookRepo, never()).save(any());
    }

    // reflection helper to get private fields
    private Object getField(Object target, String name) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(target);
    }
}
