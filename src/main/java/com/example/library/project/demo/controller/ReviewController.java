package com.example.library.project.demo.controller;

import com.example.library.project.demo.entity.DTO.UpdateReviewDTO;
import com.example.library.project.demo.entity.Review;
import com.example.library.project.demo.exception.ReviewException;
import com.example.library.project.demo.security.JwtTokenService;
import com.example.library.project.demo.service.ReviewService;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public ReviewController(ReviewService reviewService, JwtTokenService jwtTokenService) {
        this.reviewService = reviewService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @GetMapping("/all")
    public Iterable<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/my-reviews")
    public List<Review> getMyReviews(@RequestHeader("Authorization") String token) {
        Integer userId = jwtTokenService.extractUserId(token.replace("Bearer ", ""));
        return reviewService.getMyReviews(userId);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Integer id) {
        return reviewService.getReviewById(id);
    }

    @DeleteMapping("/remove/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void deleteReview(@PathVariable Integer id) {
        reviewService.deleteReview(id);
    }

    @PutMapping("/update/{id}")
    public Review updateReview(@PathVariable Integer id, @RequestBody UpdateReviewDTO dto) {
        return reviewService.updateReview(id, dto);
    }

    @GetMapping("/book/{bookId}")
    public List<Review> getReviewsByBook(@PathVariable Integer bookId) {
        return reviewService.getReviewsByBook(bookId);
    }

    @GetMapping("/user/{userId}")
    public List<Review> getReviewsByUser(@PathVariable Integer userId) {
        return reviewService.getReviewsByUser(userId);
    }

    @GetMapping("{userId}/{bookId}")
    public Review getReviewByBookAndUser(@PathVariable Integer bookId, @PathVariable Integer userId){
        return reviewService.getReviewByBookAndUser(bookId, userId);
    }
}
