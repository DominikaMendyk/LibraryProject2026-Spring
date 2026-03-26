package com.example.library.project.demo.service;

import com.example.library.project.demo.entity.Book;
import com.example.library.project.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Integer bookId) {
        bookRepository.deleteById(bookId);
    }

    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Integer bookId) {
        return bookRepository.findById(bookId);
    }

    public Book updateBook(Integer bookId, Book updatedBook) {
        return bookRepository.findById(bookId)
                .map(book -> {
                    book.setTitle(updatedBook.getTitle());
                    book.setAuthor(updatedBook.getAuthor());
                    book.setIsbn(updatedBook.getIsbn());
                    book.setPublisher(updatedBook.getPublisher());
                    book.setYear(updatedBook.getYearPublished());
                    book.setAvailableCopies(updatedBook.getAvailableCopies());
                    return bookRepository.save(book);
                }).orElseThrow(() -> new RuntimeException("Book not found"));
    }
}