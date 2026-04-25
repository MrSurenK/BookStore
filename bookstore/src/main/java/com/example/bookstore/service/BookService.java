package com.example.bookstore.service;


import com.example.bookstore.dto.AddNewBookDTO;
import com.example.bookstore.dto.AuthorIdentifierDTO;
import com.example.bookstore.dto.BookResponseDTO;
import com.example.bookstore.dto.UpdateBookDTO;
import com.example.bookstore.model.Author;
import com.example.bookstore.model.Book;
import com.example.bookstore.repo.AuthorRepo;
import com.example.bookstore.repo.BookRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bookstore.mapper.BookMapper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class BookService {

    BookRepo bookRepo;
    AuthorRepo authorRepo;

    public BookService(BookRepo bookrepo, AuthorRepo authorRepo){
        this.bookRepo = bookrepo;
        this.authorRepo = authorRepo;
    }


    //Service to add a new book
    @Transactional
    public void addNewBook(AddNewBookDTO newBookDetails){


        Set<Author> authors = checkAuthors(newBookDetails.authors());

        log.debug("Checking authors: {} ", authors);

        //Map new books details to book to create new book
       Book newBook =  Book.builder()
                        .isbn(newBookDetails.isbn())
                                .title(newBookDetails.title())
                                        .authors(authors) //Validate authors first
                                            .year(newBookDetails.year())
                                                .price(newBookDetails.price())
                                                    .genre(newBookDetails.genre())
                                                            .build();

        bookRepo.save(newBook); //save new book
        log.info("Book added");
    }


    //Helper method
    private Set<Author> checkAuthors(Set<AuthorIdentifierDTO> authors){

        Set<Author> validAuthors = new HashSet<>();

        for(AuthorIdentifierDTO author: authors){
            String authorName = author.name();
            LocalDate authorBday = author.birthday();

            Author getAuthorEntity = authorRepo.
                    findByNameAndBirthday(authorName, authorBday)
                    .orElseThrow(()-> new EntityNotFoundException("Author does not exist on our database yet." +
                            " Please add author to database"));

            validAuthors.add(getAuthorEntity);
        }
        return validAuthors;
    }


    //Service to update an existing book
    @Transactional
    public void updateBook(UpdateBookDTO updateBookDTO) {

        log.info("Attempting to update book");

        String isbn = updateBookDTO.isbn(); //get book isbn

        //Check if book exists
        log.info("Finding book with isbn: {}", isbn);
        Book book = bookRepo.findBookByIsbnAndIsDeletedFalse(isbn)
                .orElseThrow(() -> new EntityNotFoundException("Can't book with this isbn: " + isbn));

        log.info("Updating book with isbn : {}  ", isbn);

        //Update book
        //Validate authors if updating authors
        if (updateBookDTO.authors() != null && !updateBookDTO.authors().isEmpty()) {
            Set<Author>resolvedAuthors = checkAuthors(updateBookDTO.authors());
            book.setAuthors(resolvedAuthors);
            log.info("Updated authors: {}", resolvedAuthors);
        }

        //Check which fields to update
        if (updateBookDTO.title() != null && !updateBookDTO.title().isBlank()) {
            book.setTitle(updateBookDTO.title());
            log.info("Updated title: {}", updateBookDTO.title());
        }

        if (updateBookDTO.year() != null) {
            //validate that year is a valid year

            book.setYear(updateBookDTO.year());
            log.info("Updated year: {}", updateBookDTO.year());
        }

        if(updateBookDTO.price() != null){
            book.setPrice(updateBookDTO.price());
            log.info("Updated price : {}", updateBookDTO.price());
        }

        if(updateBookDTO.genre() != null && !updateBookDTO.genre().isBlank()){
            book.setGenre(updateBookDTO.genre());
            log.info("Updated genre: {}", updateBookDTO.genre());
        }

//        bookRepo.save(book);
        log.info("Book has been successfully updated and saved to database!");
    }


    public List<BookResponseDTO> findBook(String title, List<String>authorNames){
        log.info("Looking for books....");
        // Normalize title
        if (title != null && title.isBlank()) {
            title = null;
        }

        //Normalize author names
        if(authorNames != null){
            authorNames = authorNames.stream()
                    .filter(name -> name != null && !name.isBlank())
                    .map(String::toLowerCase)
                    .toList();

            if(authorNames.isEmpty()){
                authorNames = null;
            }
        }

        // Log AFTER normalization
        log.info("Searching books with title='{}' and authors={}", title, authorNames); //if null we can see here in our logs

        //Execute
        List<Book> books = bookRepo.searchBooks(title, authorNames);

        if (books.isEmpty()){
            log.info("No books found for given criteria.");
        }

        log.info("Found {} book(s)", books.size());
        return books.stream().map(BookMapper::toDTO).toList();
    }


    public void deleteBook(String isbn){
        //ISBN numbers validated at controller layer
        Book book = bookRepo.findBookByIsbnAndIsDeletedFalse(isbn).orElseThrow(()-> new EntityNotFoundException("No such book found in store"));
        log.info("Attempting to delete book : {}" , isbn);
        book.setDeleted(true);
        bookRepo.save(book);
        log.info("Book deleted");
    }


    public List<BookResponseDTO> getAllBooks(){
        log.info("Getting all books in store");
        List<Book> books = bookRepo.getAllNonDeletedBooks();
            return books.stream()
            .map(BookMapper::toDTO)
            .toList();
    }
}
