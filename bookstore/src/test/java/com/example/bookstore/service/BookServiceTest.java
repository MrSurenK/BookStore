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
import java.util.List;

import com.example.bookstore.dto.UpdateBookDTO;
import com.example.bookstore.dto.BookResponseDTO;
import com.example.bookstore.customExceptions.BadRequestException;

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
    void addNewBook_whenExistingSoftDeleted_reactivates() throws Exception {
        // arrange
        String isbn = "1111111111111";
        
        String newTitle = "Reactivated Title";
        String authorName = "John Smith";
        LocalDate bday = LocalDate.of(1970, 2, 2);

        // existing soft-deleted book
        Author existingAuthor = Author.builder().id(2L).name(authorName).birthday(bday).books(Set.of()).build();
        Book existing = Book.builder().isbn(isbn).title("Old Title").authors(Set.of(existingAuthor)).year(2000).price(5.0).genre("Old").build();
        // mark deleted via reflection
        java.lang.reflect.Field delField = existing.getClass().getDeclaredField("isDeleted");
        delField.setAccessible(true);
        delField.set(existing, true);

        when(bookRepo.findById(isbn)).thenReturn(Optional.of(existing));
        when(authorRepo.findByNameAndBirthday(authorName, bday)).thenReturn(Optional.of(existingAuthor));

        AddNewBookDTO dto = new AddNewBookDTO(isbn, newTitle, Set.of(new AuthorIdentifierDTO(authorName, bday)), 2021, 15.0, "NewGenre");

        // act
        bookService.addNewBook(dto);

        // assert: book reactivated and saved
        verify(bookRepo, times(1)).save(existing);
        assertFalse((Boolean) getField(existing, "isDeleted"));
        assertEquals(newTitle, getField(existing, "title"));
        assertEquals(2021, getField(existing, "year"));
    }

    @Test
    void addNewBook_whenActiveDuplicate_throwsBadRequest() {
        // arrange
        String isbn = "2222222222222";
        Book existing = Book.builder().isbn(isbn).title("Active").build();
        // ensure not deleted (default false)
        when(bookRepo.findById(isbn)).thenReturn(Optional.of(existing));

        AddNewBookDTO dto = new AddNewBookDTO(isbn, "Any", Set.of(), 2020, 1.0, "G");

        // act & assert
        assertThrows(BadRequestException.class, () -> bookService.addNewBook(dto));
    }

    @Test
    void updateBook_updatesFields_whenPresent() throws Exception {
        // arrange
        String isbn = "3333333333333";
        Book existing = Book.builder().isbn(isbn).title("T1").year(2000).price(10.0).genre("G1").build();
        when(bookRepo.findBookByIsbnAndIsDeletedFalse(isbn)).thenReturn(Optional.of(existing));

        UpdateBookDTO dto = new UpdateBookDTO(isbn, "T2", null, 2022, 12.5, "G2");

        // act
        bookService.updateBook(dto);

        // assert fields updated on the same entity
        assertEquals("T2", getField(existing, "title"));
        assertEquals(2022, getField(existing, "year"));
        assertEquals(12.5, (double) getField(existing, "price"), 0.0001);
        assertEquals("G2", getField(existing, "genre"));
    }

    @Test
    void updateBook_whenMissing_throwsEntityNotFound() {
        String isbn = "4444444444444";
        when(bookRepo.findBookByIsbnAndIsDeletedFalse(isbn)).thenReturn(Optional.empty());
        UpdateBookDTO dto = new UpdateBookDTO(isbn, null, null, null, null, null);
        assertThrows(EntityNotFoundException.class, () -> bookService.updateBook(dto));
    }

    @Test
    void deleteBook_marksDeleted_andSaves() throws Exception {
        String isbn = "5555555555555";
        Book existing = Book.builder().isbn(isbn).title("ToDelete").build();
        when(bookRepo.findBookByIsbnAndIsDeletedFalse(isbn)).thenReturn(Optional.of(existing));

        bookService.deleteBook(isbn);

        // verify saved and flag set
        verify(bookRepo, times(1)).save(existing);
        assertTrue((Boolean) getField(existing, "isDeleted"));
    }

    @Test
    void findBook_returnsMappedDTOs() {
        // arrange
        Book b = Book.builder().isbn("9780306406157").title("SearchMe").authors(Set.of()).year(2020).price(9.99).genre("Tech").build();
        // ensure authors is non-null for mapping (builder may leave it null in some Lombok setups)
        b.setAuthors(Set.of());
        when(bookRepo.searchBooks("SearchMe", null)).thenReturn(List.of(b));

        // act
        List<BookResponseDTO> res = bookService.findBook("SearchMe", null);

        // assert
        assertEquals(1, res.size());
        assertEquals("9780306406157", res.stream().findFirst().orElseThrow().isbn());
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



