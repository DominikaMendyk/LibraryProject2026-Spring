package com.example.library.project.demo.controller;

import com.example.library.project.demo.entity.Book;
import com.example.library.project.demo.entity.DTO.BookLoanDTO;
import com.example.library.project.demo.entity.DTO.LoanHistoryDTO;
import com.example.library.project.demo.entity.Loan;
import com.example.library.project.demo.entity.User;
import com.example.library.project.demo.security.JwtTokenService;
import com.example.library.project.demo.service.LoanService;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loan")
public class LoanController {

    private final LoanService loanService;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public LoanController(LoanService loanService, JwtTokenService jwtTokenService) {
        this.loanService = loanService;
        this.jwtTokenService = jwtTokenService;
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
    public List<LoanHistoryDTO> getCurrentlyBorrowedBooks(@PathVariable Integer userId) {
        return loanService.getCurrentlyBorrowedBooks(userId);
    }

    @GetMapping("/user/{userId}/history")
    public List<LoanHistoryDTO> getPreviouslyBorrowedBooks(@PathVariable Integer userId) {
        return loanService.getPreviouslyBorrowedBooks(userId);
    }
    
    @GetMapping("/book/{bookId}/current-users")
    public List<BookLoanDTO> getUsersCurrentlyBorrowedBook(@PathVariable Integer bookId) {
        return loanService.getUsersCurrentlyBorrowedBook(bookId);
    }

    @GetMapping("/book/{bookId}/history-users")
    public List<BookLoanDTO> getUsersPreviouslyBorrowedBook(@PathVariable Integer bookId) {
        return loanService.getUsersPreviouslyBorrowedBook(bookId);
    }

    @GetMapping("/all")
    public List<LoanHistoryDTO> getAllLoans() {
        return loanService.getAllLoans();
    }

    @GetMapping("book/{bookId}")
    public List<LoanHistoryDTO> getAllLoansForABook(@PathVariable Integer bookId) {
        return loanService.getAllLoansForABook(bookId);
    }

    @GetMapping("/status/{bookId}")
    public ResponseEntity<Boolean> hasCurrentlyBorrowed(
            @PathVariable Integer bookId,
            @RequestHeader("Authorization") String token
    ) {
        Integer userId = jwtTokenService.extractUserId(token.replace("Bearer ", ""));

        return ResponseEntity.ok(loanService.hasActiveLoan(userId, bookId));
    }

    @GetMapping("/my/{bookId}/history")
    public ResponseEntity<Boolean> hasPreviouslyBorrowed(
            @PathVariable Integer bookId,
            @RequestHeader("Authorization") String token
    ) {
        Integer userId = jwtTokenService.extractUserId(token.replace("Bearer ", ""));

        return ResponseEntity.ok(loanService.hasHistoryLoan(userId, bookId));
    }
}