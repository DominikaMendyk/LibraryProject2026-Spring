package com.example.library.project.demo.controller;

import com.example.library.project.demo.entity.Review;
import com.example.library.project.demo.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @GetMapping("/getAll")
    public Iterable<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Integer id) {
        return reviewService.getReviewById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    @DeleteMapping("/remove/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void deleteReview(@PathVariable Integer id) {
        reviewService.deleteReview(id);
    }

    @PutMapping("/update/{id}")
    public Review updateReview(@PathVariable Integer id, @RequestBody Review review) {
        return reviewService.updateReview(id, review);
    }

    @GetMapping("/book/{bookId}")
    public List<Review> getReviewsByBook(@PathVariable Integer bookId) {
        return reviewService.getReviewsByBook(bookId);
    }

    @GetMapping("/user/{userId}")
    public List<Review> getReviewsByUser(@PathVariable Integer userId) {
        return reviewService.getReviewsByUser(userId);
    }
}
