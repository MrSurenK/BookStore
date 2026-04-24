package com.example.bookstore.service;


import com.example.bookstore.dto.AddNewBookDTO;
import com.example.bookstore.dto.AuthorIdentifierDTO;
import com.example.bookstore.dto.UpdateBookDTO;
import com.example.bookstore.model.Author;
import com.example.bookstore.model.Book;
import com.example.bookstore.repo.AuthorRepo;
import com.example.bookstore.repo.BookRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.HashSet;
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
    }

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
    public void updateBook(UpdateBookDTO updateBookDTO){

        String isbn = updateBookDTO.isbn(); //get book isbn

         //Check if book exists
        log.info("Finding book with isbn: {}", isbn);
        Book book = bookRepo.findBookByIdAndIsDeletedFalse(isbn)
                .orElseThrow(()-> new EntityNotFoundException("Can't book with this isbn: " + isbn));

        //Update book
        //Validate authors if updating authors
        Set<Author> resolvedAuthors = null;
        if(updateBookDTO.authors() != null){
            resolvedAuthors = checkAuthors(updateBookDTO.authors());
        }

        //Check which fields to update
        if(updateBookDTO.title() != null){
            book.setTitle(updateBookDTO.title());
        }

//        if(updateBookDTO.year() != null){
//            //validate that year is a valid year

            book.setYear(updateBookDTO.year());
        }



    }
