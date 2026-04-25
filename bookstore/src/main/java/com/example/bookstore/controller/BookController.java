package com.example.bookstore.controller;


import com.example.bookstore.dto.AddNewBookDTO;
import com.example.bookstore.dto.BookResponseDTO;
import com.example.bookstore.dto.Res;
import com.example.bookstore.dto.UpdateBookDTO;
import com.example.bookstore.model.Book;
import com.example.bookstore.service.BookService;
import com.example.bookstore.utility.ValidISBN;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookController {

    BookService bookService;


    public BookController(BookService bookService){
        this.bookService = bookService;
    }

    @PostMapping("/addNew")
    @PreAuthorize("hasAnyRole('MANAGER','OWNER')")
    public ResponseEntity<Res<Object>> addNewBook(@RequestBody AddNewBookDTO request){

        //call service to add new book
        bookService.addNewBook(request); //process add new book request
        //Exceptions handled in global exception handler
        return ResponseEntity.status(201).body(Res.success(null, "Book created successfully")
        );
    }


    @PatchMapping("/updateBook")
    @PreAuthorize("hasAnyRole('MANAGER','OWNER')")
    public ResponseEntity<Res<Object>> updateExistingBook(@Valid @RequestBody UpdateBookDTO updateBookDTO){
        bookService.updateBook(updateBookDTO);

        return ResponseEntity.status(200).body(Res.success(null, "Book updated successfully"));
    }


    //To be RESTful pass search criteria as param
    @GetMapping("/searchStore")
    public ResponseEntity<Res<Object>>  lookForBook(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> authorNames
    ){

        List<BookResponseDTO> books = bookService.findBook(title, authorNames);

        return ResponseEntity.status(200).body(Res.success(books, "Search completed"));
    }

    @DeleteMapping("/{isbn}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Res<Object>> deleteBookApi(@PathVariable  @ValidISBN String isbn){

        bookService.deleteBook(isbn);

        return ResponseEntity.status(200).body(Res.success(null, "Book deleted"));
    }


    @GetMapping("/allBooks")
    public ResponseEntity<Res<Object>> getAllBooks(){
        List<BookResponseDTO> books = bookService.getAllBooks();
        return ResponseEntity.status(200).body(Res.success(books, "All books available in store"));
    }

}



