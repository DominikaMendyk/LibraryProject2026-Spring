package com.example.library.project.demo.entity;
import jakarta.persistence.*;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer bookId;

    @Column(unique=true, nullable=false)
    private String isbn;

    @Column(nullable=false)
    private String title;

    @Column(nullable=false)
    private String author;

    @Column(nullable=false)
    private String publisher;

    @Column(nullable=false)
    private Integer yearPublished;

    @Column(nullable=false)
    private Integer availableCopies;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private BookFormat bookFormat;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getYearPublished() {
        return yearPublished;
    }

    public void setYear(Integer yearPublished) {
        this.yearPublished = yearPublished;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }

    public BookFormat getBookFormat(){
        return bookFormat;
    }

    public void setBookFormat(BookFormat bookFormat){
        this.bookFormat = bookFormat;
    }
}
