package com.example.UberComp.service;

import com.example.UberComp.dto.review.CreateReviewDTO;
import com.example.UberComp.dto.review.CreatedReviewDTO;
import com.example.UberComp.model.Review;
import com.example.UberComp.model.Ride;
import com.example.UberComp.model.User;
import com.example.UberComp.repository.ReviewRepository;
import com.example.UberComp.repository.RideRepository;
import com.example.UberComp.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RideRepository rideRepository;

    public CreatedReviewDTO createReview(CreateReviewDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ride ride = rideRepository.findById(dto.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        Review review = new Review(dto, user, ride);

        Review saved = reviewRepository.save(review);

        return new CreatedReviewDTO(saved);    }
}
