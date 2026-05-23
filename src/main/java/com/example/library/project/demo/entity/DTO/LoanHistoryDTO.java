package com.example.library.project.demo.entity.DTO;
import java.time.LocalDate;

public class LoanHistoryDTO {
    private Integer bookId;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Integer userId;
    private LocalDate loanDate;
    private LocalDate returnDate;

    public LoanHistoryDTO(Integer bookId, String isbn, String title, String author, String publisher, Integer userId, LocalDate loanDate, LocalDate returnDate) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.userId = userId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }
    public Integer getBookId() { return bookId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public Integer getUserId() { return userId; }
    public LocalDate getLoanDate() { return loanDate; }
    public LocalDate getReturnDate() { return returnDate; }
}
