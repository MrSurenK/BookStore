package com.example.bookstore.controller;


import com.example.bookstore.dto.AddNewBookDTO;
import com.example.bookstore.dto.Res;
import com.example.bookstore.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/book")
public class BookController {

    BookService bookService;


    public BookController(BookService bookService){
        this.bookService = bookService;
    }

    @PostMapping("/addNew")
    public ResponseEntity<Res<Object>> addNewBook(AddNewBookDTO request){

        //call service to add new book
        bookService.addNewBook(request); //process add new book request
        //Exceptions handled in global exception handler
        return ResponseEntity.status(201).body(Res.success(null, "Book created successfully")
        );

    }

}



