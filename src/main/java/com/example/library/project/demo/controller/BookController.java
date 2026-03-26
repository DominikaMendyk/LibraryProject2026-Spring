package com.example.library.project.demo.controller;

import com.example.library.project.demo.entity.Book;
import com.example.library.project.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/book")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/add")
    @ResponseStatus(code = HttpStatus.CREATED) //code 201
    public Book addBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    @DeleteMapping("/remove/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void removeBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
    }

    @GetMapping("/getAll")
    public Iterable<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Integer id) {
        Optional<Book> book = bookService.getBookById(id);
        return book.orElseThrow(() -> new RuntimeException("Book not found"));
    }

    @PutMapping("/update/{id}")
    public Book updateBook(@PathVariable Integer id, @RequestBody Book updatedBook) {
        return bookService.updateBook(id, updatedBook);
    }
}
