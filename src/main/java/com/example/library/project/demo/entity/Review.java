package com.example.library.project.demo.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Date;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer reviewId;

    @ManyToOne
    @JoinColumn(name = "bookId", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;

    @Column(nullable = false)
    private Date reviewDate;

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public @Min(1) @Max(5) Integer getRating() {
        return rating;
    }

    public void setRating(@Min(1) @Max(5) Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }
}
