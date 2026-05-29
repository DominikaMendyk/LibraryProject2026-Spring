package com.example.library.project.demo.service;

import com.example.library.project.demo.entity.Book;
import com.example.library.project.demo.entity.DTO.BookLoanDTO;
import com.example.library.project.demo.entity.DTO.LoanHistoryDTO;
import com.example.library.project.demo.entity.Loan;
import com.example.library.project.demo.entity.User;
import com.example.library.project.demo.exception.LoanException;
import com.example.library.project.demo.repository.BookRepository;
import com.example.library.project.demo.repository.LoanRepository;
import com.example.library.project.demo.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    // needs to be lazy to avoid cycles, but it's a bit ugly
    private final UserService userService;
    private final BookRepository bookRepository;


    @Autowired
    public LoanService(LoanRepository loanRepository, UserRepository userRepository, @Lazy UserService userService, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Loan borrowBook(Integer userId, Integer bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> LoanException.create("User not found"));

        Book book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> LoanException.create("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw LoanException.create("No copies available");
        }
        // User cannot borrow a book if they have a credit
        if (userService.getTotalCredit(userId) > 0){
            throw LoanException.create("Cannot borrow a book with a positive credit");
        }

        // To check if it has been already borrowed
        if (hasActiveLoan(userId, bookId)){
            throw LoanException.create("Cannot borrow a currently borrowed book");
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

    //default
    @Transactional
    public Loan returnBook(int userId, Integer bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> LoanException.create("User not found"));

        Book book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> LoanException.create("Book not found"));

        Loan loan = loanRepository
                .findByBookAndUserAndReturnDateIsNull(book, user)
                .orElseThrow(() -> LoanException.create("Book was never borrowed or already returned"));

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
    /*
        used for debugging
    @Transactional
    public Loan returnBook(int userId, String isbn) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> LoanException.create("User not found"));

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> LoanException.create("Book not found"));

        Loan loan = loanRepository
                .findFirstByBookAndUserAndReturnDateIsNullOrderByLoanDateAsc(book, user)
                .orElseThrow(() ->
                        LoanException.create("Book was never borrowed or already returned"));

        LocalDate currentDate = LocalDate.now();
        LocalDate dueDate = loan.getDueDate().toLocalDate();

        if (dueDate.isBefore(currentDate)) {

            int daysOverdue =
                    (int) Math.ceil(ChronoUnit.DAYS.between(dueDate, currentDate));

            int credit = daysOverdue / 2;

            user.updateCredit(credit);
        }

        loan.setReturnDate(currentDate);

        book.setAvailableCopies(book.getAvailableCopies() + 1);

        bookRepository.save(book);
        userRepository.save(user);

        return loanRepository.save(loan);
    }

     */

    // Find all currently borrowed books by a userId
    public List<LoanHistoryDTO> getCurrentlyBorrowedBooks(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> LoanException.create("User not found"));

        return loanRepository.findByUser_UserIdAndReturnDateIsNull(userId)
                .stream()
                .map(loan -> new LoanHistoryDTO(
                        loan.getBook().getBookId(),
                        loan.getBook().getIsbn(),
                        loan.getBook().getTitle(),
                        loan.getBook().getAuthor(),
                        loan.getBook().getPublisher(),
                        loan.getUser().getUserId(),
                        loan.getLoanDate(),
                        null
                ))
                .collect(Collectors.toList());
    }

    // Find all previously borrowed books by a userId
    public List<LoanHistoryDTO> getPreviouslyBorrowedBooks(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> LoanException.create("User not found"));

        return loanRepository.findByUser_UserIdAndReturnDateIsNotNull(userId)
                .stream()
                .map(loan -> new LoanHistoryDTO(
                        loan.getBook().getBookId(),
                        loan.getBook().getIsbn(),
                        loan.getBook().getTitle(),
                        loan.getBook().getAuthor(),
                        loan.getBook().getPublisher(),
                        loan.getUser().getUserId(),
                        loan.getLoanDate(),
                        loan.getReturnDate()
                ))
                .collect(Collectors.toList());
    }

    // Find all users who have currently borrowed a book by its bookId
    public List<BookLoanDTO> getUsersCurrentlyBorrowedBook(Integer bookId) {
        Book book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> LoanException.create("Book not found"));
        return loanRepository.findByBookAndReturnDateIsNull(book)
                .stream()
                .map(loan -> new BookLoanDTO(
                        loan.getUser().getUserId(),
                        loan.getLoanDate(),
                        null
                ))
                .collect(Collectors.toList());
    }

    // Find all users who have previously borrowed a book by its bookId
    public List<BookLoanDTO> getUsersPreviouslyBorrowedBook(Integer bookId) {
        Book book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> LoanException.create("Book not found"));
        return loanRepository.findByBookAndReturnDateIsNotNull(book)
                .stream()
                .map(loan -> new BookLoanDTO(
                        loan.getUser().getUserId(),
                        loan.getLoanDate(),
                        loan.getReturnDate()
                ))
                .collect(Collectors.toList());
    }

    public List<LoanHistoryDTO> getAllLoans() {
        return StreamSupport
                .stream(loanRepository.findAll().spliterator(), false)
                .map(loan -> new LoanHistoryDTO(
                        loan.getBook().getBookId(),
                        loan.getBook().getIsbn(),
                        loan.getBook().getTitle(),
                        loan.getBook().getAuthor(),
                        loan.getBook().getPublisher(),
                        loan.getUser().getUserId(),
                        loan.getLoanDate(),
                        loan.getReturnDate()
                ))
                .collect(Collectors.toList());
    }

    public Boolean hasActiveLoan(Integer userId, Integer bookId) {
        if (bookId == null || userId == null) {
            return false;
        }
        Book book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<LoanHistoryDTO> currentlyBorrowedBooks = getCurrentlyBorrowedBooks(userId);
        if (currentlyBorrowedBooks.isEmpty()){
            return false;
        }
        return currentlyBorrowedBooks.stream()
                .anyMatch(loan -> bookId.equals(loan.getBookId()));
    }

    // only previously borrowed books - not current
    public Boolean hasHistoryLoan(Integer userId, Integer bookId) {
        if (bookId == null || userId == null) {
            return false;
        }
        Book book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<LoanHistoryDTO> previouslyBorrowedBooks = getPreviouslyBorrowedBooks(userId);
        if (previouslyBorrowedBooks.isEmpty()){
            return false;
        }
        return previouslyBorrowedBooks.stream()
                .anyMatch(loan -> bookId.equals(loan.getBookId()));
    }
}