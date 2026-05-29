package com.example.library.project.demo.service;

import com.example.library.project.demo.entity.DTO.UpdateReviewDTO;
import com.example.library.project.demo.entity.Review;
import com.example.library.project.demo.exception.ReviewException;
import com.example.library.project.demo.repository.ReviewRepository;
import com.example.library.project.demo.security.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final LoanService loanService;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                         LoanService loanService, JwtTokenService jwtTokenService) {
        this.reviewRepository = reviewRepository;
        this.loanService = loanService;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public Review addReview(Review review) {
        if (review == null){
            throw ReviewException.create("Cannot add null review.");
        }
        if (review.getBook() == null || review.getUser() == null) {
            throw ReviewException.create("Review must contain a book and user.");
        }
        Integer bookId = review.getBook().getBookId();
        Integer userId = review.getUser().getUserId();

        boolean hasBorrowed =
                loanService.hasActiveLoan(userId, bookId)
                        || loanService.hasHistoryLoan(userId, bookId);

        if (!hasBorrowed) {
            throw ReviewException.create(
                    "You can only review books you have borrowed."
            );
        }

        boolean alreadyReviewed = reviewRepository
                .findByBook_BookIdAndUser_UserId(bookId, userId)
                .isPresent();

        if (alreadyReviewed) {
            throw ReviewException.create("You have already reviewed this book.");
        }
        return reviewRepository.save(review);
    }

    public Iterable<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getMyReviews(Integer userId) {
        return reviewRepository.findByUser_UserId(userId);
    }

    public Review getReviewById(Integer reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> ReviewException.create("Review with ID " + reviewId + " not found"));
    }

    @Transactional
    public void deleteReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                        .orElseThrow(() -> ReviewException.create("Cannot delete a non existing review."));
        reviewRepository.delete(review);
    }

    @Transactional
    public Review updateReview(Integer reviewId, UpdateReviewDTO dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> ReviewException.create("Review not found"));
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        return reviewRepository.save(review);
    }

    /*
    public List<Review> getReviewsByBook(Integer bookId) {
        List<Review> reviews = reviewRepository.findByBook_BookId(bookId);
        if (reviews.isEmpty()){
            throw ReviewException.create("No reviews found for book with ID " + bookId);
        }
        return reviews;
    }

     */
    public List<Review> getReviewsByBook(Integer bookId) {
        return reviewRepository.findByBook_BookId(bookId);
    }


    public List<Review> getReviewsByUser(Integer userId) {
        List<Review> reviews = reviewRepository.findByUser_UserId(userId);
        if (reviews.isEmpty()){
            throw ReviewException.create("User with ID " + userId + "did not write any reviews");
        }
        return reviews;
    }

    public Review getReviewByBookAndUser(Integer bookId, Integer userId) {
        Review review = reviewRepository.findReviewByBook_BookIdAndUser_UserId(bookId, userId);
        if (review == null){
            throw ReviewException.create("User with ID " + userId + "did not write any reviews");
        }
        return review;
    }
}