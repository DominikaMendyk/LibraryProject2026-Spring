package com.example.library.project.demo.controller;

import com.example.library.project.demo.entity.Book;
import com.example.library.project.demo.entity.Loan;
import com.example.library.project.demo.entity.User;
import com.example.library.project.demo.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loan")
public class LoanController {

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/borrow")
    @ResponseStatus(HttpStatus.CREATED)
    public Loan borrowBook(@RequestParam Integer userId,
                           @RequestParam Integer bookId) {
        return loanService.borrowBook(userId, bookId);
    }

    @PostMapping("/return")
    public Loan returnBook(@RequestParam Integer userId,
                           @RequestParam Integer bookId) {
        return loanService.returnBook(userId, bookId);
    }

    @GetMapping("/user/{userId}/current")
    public List<Book> getCurrentlyBorrowedBooks(@PathVariable Integer userId) {
        return loanService.getCurrentlyBorrowedBooks(userId);
    }

    @GetMapping("/user/{userId}/history")
    public List<Book> getPreviouslyBorrowedBooks(@PathVariable Integer userId) {
        return loanService.getPreviouslyBorrowedBooks(userId);
    }
    
    @GetMapping("/book/{bookId}/current-users")
    public List<User> getUsersCurrentlyBorrowedBook(@PathVariable Integer bookId) {
        return loanService.getUsersCurrentlyBorrowedBook(bookId);
    }

    @GetMapping("/book/{bookId}/history-users")
    public List<User> getUsersPreviouslyBorrowedBook(@PathVariable Integer bookId) {
        return loanService.getUsersPreviouslyBorrowedBook(bookId);
    }
}