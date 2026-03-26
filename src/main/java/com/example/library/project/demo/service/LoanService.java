package com.example.library.project.demo.service;

import com.example.library.project.demo.entity.Book;
import com.example.library.project.demo.entity.Loan;
import com.example.library.project.demo.entity.User;
import com.example.library.project.demo.repository.BookRepository;
import com.example.library.project.demo.repository.LoanRepository;
import com.example.library.project.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public Loan borrowBook(int userId, int bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No copies available");
        }
        // User cannot borrow a book if they have a credit
        if (user.getCredit() > 0){
            throw new RuntimeException("Cannot borrow a book with a positive credit");
        } else {
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            Loan loan = new Loan();
            loan.setBook(book);
            loan.setUser(user);
            LocalDate currentDate = LocalDate.now();
            loan.setLoanDate(currentDate);
            LocalDateTime dueDate = currentDate
                    .plusDays(30)
                    .atTime(LocalTime.of(23, 59));
            loan.setDueDate(dueDate); // 30 days
            return loanRepository.save(loan);
        }
    }

    public Loan returnBook(int userId, int bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Loan loan = loanRepository
                .findByBookAndUserAndReturnDateIsNull(book, user)
                .orElseThrow(() -> new RuntimeException("Book was never borrowed or already returned"));

        LocalDate currentDate = LocalDate.now();
        LocalDate dueDate = loan.getDueDate().toLocalDate();
        if (dueDate.isBefore(currentDate)) {
            // User credit increased - my own idea to implement
            int daysOverdue = (int)  Math.ceil(ChronoUnit.DAYS.between(dueDate, currentDate));
            int credit = daysOverdue/2;
            user.updateCredit(credit);
            loan.setReturnDate(currentDate);
        } else{
            loan.setReturnDate(currentDate);
        }
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        return loanRepository.save(loan);
    }

    // Find all currently borrowed books by a userId
    public List<Book> getCurrentlyBorrowedBooks(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return loanRepository.findByUserAndReturnDateIsNull(user)
                .stream()
                .map(Loan::getBook)
                .collect(Collectors.toList());
    }

    // Find all previously borrowed books by a userId
    public List<Book> getPreviouslyBorrowedBooks(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return loanRepository.findByUserAndReturnDateIsNotNull(user)
                .stream()
                .map(Loan::getBook)
                .collect(Collectors.toList());
    }

    // Find all users who have currently borrowed a book by its id
    public List<User> getUsersCurrentlyBorrowedBook(int bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return loanRepository.findByBookAndReturnDateIsNull(book)
                .stream()
                .map(Loan::getUser)
                .collect(Collectors.toList());
    }

    // Find all users who have previously borrowed a book by its id
    public List<User> getUsersPreviouslyBorrowedBook(int bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return loanRepository.findByBookAndReturnDateIsNotNull(book)
                .stream()
                .map(Loan::getUser)
                .collect(Collectors.toList());
    }

}