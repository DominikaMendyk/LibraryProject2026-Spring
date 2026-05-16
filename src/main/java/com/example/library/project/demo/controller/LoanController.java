package com.example.library.project.demo.controller;

import com.example.library.project.demo.entity.Book;
import com.example.library.project.demo.entity.DTO.BookLoanDTO;
import com.example.library.project.demo.entity.DTO.LoanHistoryDTO;
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
                           @RequestParam String isbn) {
        return loanService.borrowBook(userId, isbn);
    }

    @PostMapping("/return")
    public Loan returnBook(@RequestParam Integer userId,
                           @RequestParam String isbn) {
        return loanService.returnBook(userId, isbn);
    }

    @GetMapping("/user/{userId}/current")
    public List<LoanHistoryDTO> getCurrentlyBorrowedBooks(@PathVariable Integer userId) {
        return loanService.getCurrentlyBorrowedBooks(userId);
    }

    @GetMapping("/user/{userId}/history")
    public List<LoanHistoryDTO> getPreviouslyBorrowedBooks(@PathVariable Integer userId) {
        return loanService.getPreviouslyBorrowedBooks(userId);
    }
    
    @GetMapping("/book/{isbn}/current-users")
    public List<BookLoanDTO> getUsersCurrentlyBorrowedBook(@PathVariable String isbn) {
        return loanService.getUsersCurrentlyBorrowedBook(isbn);
    }

    @GetMapping("/book/{isbn}/history-users")
    public List<BookLoanDTO> getUsersPreviouslyBorrowedBook(@PathVariable String isbn) {
        return loanService.getUsersPreviouslyBorrowedBook(isbn);
    }

    @GetMapping("/all")
    public List<LoanHistoryDTO> getAllLoans() {
        return loanService.getAllLoans();
    }
}