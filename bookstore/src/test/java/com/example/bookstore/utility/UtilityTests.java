package com.example.bookstore.utility;

import com.example.bookstore.dto.BookResponseDTO;
import com.example.bookstore.dto.AuthorResDTO;
import com.example.bookstore.model.Author;
import com.example.bookstore.model.Book;
import com.example.bookstore.validator.ISBNValidator;
import com.example.bookstore.validator.YearValidator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTests {

    @Test
    void isbnValidator_validAndInvalidCases() {
        ISBNValidator v = new ISBNValidator();

        // null is invalid per implementation
        assertFalse(v.isValid(null, null));

        // valid ISBN-13 (from seed)
        assertTrue(v.isValid("9780306406157", null));

        // valid ISBN-10
        assertTrue(v.isValid("0306406152", null));

        // with hyphens
        assertTrue(v.isValid("978-0-306-40615-7", null));

        // invalid examples
        assertFalse(v.isValid("1234567890", null));
        assertFalse(v.isValid("9780306406158", null));
        assertFalse(v.isValid("", null));
    }

    @Test
    void yearValidator_acceptsPastAndRejectsFuture() {
        YearValidator yv = new YearValidator();

        assertTrue(yv.isValid(null, null)); // null allowed

        int current = Year.now().getValue();
        assertTrue(yv.isValid(current, null));
        assertTrue(yv.isValid(current - 10, null));
        assertFalse(yv.isValid(current + 1, null));
    }

    @Test
    void bookMapper_mapsBookToDto_andAuthors() {
        Author a = Author.builder()
                .id(1L)
                .name("Jane Doe")
                .birthday(LocalDate.of(1980,1,1))
                .books(Set.of())
                .build();

        Book b = Book.builder()
                .isbn("9780306406157")
                .title("Mapped Book")
                .authors(Set.of(a))
                .year(2020)
                .price(19.99)
                .genre("Tech")
                .build();

        BookResponseDTO dto = BookMapper.toDTO(b);

        assertEquals(b.getIsbn(), dto.isbn());
        assertEquals(b.getTitle(), dto.title());
        assertEquals(b.getYear(), dto.year());
        assertEquals(b.getPrice(), dto.price(), 0.0001);
        assertEquals(b.getGenre(), dto.genre());
        assertNotNull(dto.authors());
        assertEquals(1, dto.authors().size());
        AuthorResDTO ar = dto.authors().get(0);
        assertEquals(a.getName(), ar.name());
        assertEquals(a.getBirthday(), ar.birthday());
    }
}



