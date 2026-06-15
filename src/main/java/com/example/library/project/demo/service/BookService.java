package com.example.library.project.demo.service;

import com.example.library.project.demo.entity.*;
import com.example.library.project.demo.exception.BookException;
import com.example.library.project.demo.exception.LoginPasswordException;
import com.example.library.project.demo.repository.BookRepository;
import com.example.library.project.demo.repository.LoanRepository;
import com.example.library.project.demo.repository.ReviewRepository;
import com.example.library.project.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public BookService(BookRepository bookRepository, UserRepository userRepository,
                       LoanRepository loanRepository, ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Book addBook(Book book) {
        Optional<Book> list = bookRepository.findByIsbn(book.getIsbn());
        if (list.isPresent()){
            Book bookOld = list.get();
            // check if all other info besides copies is the same
            if (Objects.equals(book.getAuthor(), bookOld.getAuthor())
                && Objects.equals(book.getPublisher(), bookOld.getPublisher())
                && Objects.equals(book.getTitle(), bookOld.getTitle())
                && Objects.equals(book.getYearPublished(), bookOld.getYearPublished())) {
                int oldCopies = bookOld.getAvailableCopies();
                int newCopies = book.getAvailableCopies();
                bookOld.setAvailableCopies(oldCopies + newCopies);
                return bookRepository.save(bookOld);
            }
            else throw BookException.create("Book with this ISBN already exists," +
                    "and the Author, Publisher, Title or Year Published data differ.");
        } else{
            return bookRepository.save(book);
        }
    }

    @Transactional
    public String deleteBook(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> BookException.create("Cannot delete a book with this isbn." +
                        "There is none in the library with this isbn."));

        int bookId = book.getBookId();
        List<Loan> activeLoans = loanRepository.findByBook_BookIdAndReturnDateIsNull(bookId);
        for (Loan loan : activeLoans) {
            User user = loan.getUser();
            if (user != null) {
                // it's treated as if the user bought this book
                user.setCredit(user.getCredit() + 100);
                userRepository.save(user);
            }
        }
        reviewRepository.deleteByBook_BookId(bookId);
        loanRepository.deleteByBook_BookId(bookId);
        bookRepository.delete(book);
        return ("Successfully deleted book with isbn " + isbn);
    }

    @Transactional
    public Book removeCopies(String isbn, Integer copiesToRemove) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> BookException.create(
                        "Cannot remove copies. Book with this ISBN does not exist."));
        if (copiesToRemove <= 0) {
            throw BookException.create("Number of copies to remove must be greater than 0");
        }
        if (book.getAvailableCopies() < copiesToRemove) {
            throw BookException.create("Not enough copies to remove. Available copies: " + book.getAvailableCopies());
        }
        book.setAvailableCopies(book.getAvailableCopies() - copiesToRemove);
        return bookRepository.save(book);
    }

    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(()-> BookException.create(
                        "There is no book in the library with this isbn."));
    }

    @Transactional
    public Book updateBook(String isbn, Book updatedBook) {
        return bookRepository.findByIsbn(isbn)
                .map(book -> {
                    book.setTitle(updatedBook.getTitle());
                    book.setAuthor(updatedBook.getAuthor());
                    book.setPublisher(updatedBook.getPublisher());
                    book.setYear(updatedBook.getYearPublished());
                    book.setAvailableCopies(updatedBook.getAvailableCopies());
                    return bookRepository.save(book);
                }).orElseThrow(() -> BookException.create("Book not found, cannot be updated"));
    }
}