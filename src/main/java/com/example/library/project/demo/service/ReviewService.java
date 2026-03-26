package com.example.library.project.demo.service;

import com.example.library.project.demo.entity.Review;
import com.example.library.project.demo.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    public Iterable<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Optional<Review> getReviewById(Integer reviewId) {
        return reviewRepository.findById(reviewId);
    }

    public void deleteReview(Integer reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public Review updateReview(Integer reviewId, Review updatedReview) {
        return reviewRepository.findById(reviewId)
                .map(review -> {
                    review.setRating(updatedReview.getRating());
                    review.setComment(updatedReview.getComment());
                    review.setReviewDate(updatedReview.getReviewDate());
                    review.setBook(updatedReview.getBook());
                    review.setUser(updatedReview.getUser());
                    return reviewRepository.save(review);
                })
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    public List<Review> getReviewsByBook(Integer bookId) {
        return reviewRepository.findByBook_BookId(bookId);
    }

    public List<Review> getReviewsByUser(Integer userId) {
        return reviewRepository.findByUser_UserId(userId);
    }
}