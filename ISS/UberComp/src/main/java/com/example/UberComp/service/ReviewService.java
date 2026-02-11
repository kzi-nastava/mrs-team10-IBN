package com.example.UberComp.service;

import com.example.UberComp.dto.review.CreateReviewDTO;
import com.example.UberComp.dto.review.CreatedReviewDTO;
import com.example.UberComp.dto.review.GetReviewDTO;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;

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

    @Transactional
    public CreatedReviewDTO createReview(CreateReviewDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ride ride = rideRepository.findById(dto.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!ride.getPassengers().contains(user)) {
            throw new ResponseStatusException(FORBIDDEN, "You are not allowed to rate this ride");
        }
        Review review = new Review(dto, user, ride);

        Review saved = reviewRepository.save(review);

        return new CreatedReviewDTO(saved);    }

    public List<GetReviewDTO> getReviewsForRide(Long RideId){
        List<Review> rawReviews = reviewRepository.findAllByRideIdOrderByIdDesc(RideId);
        List<GetReviewDTO> reviews = rawReviews.stream().map(GetReviewDTO::new).toList();
        return reviews;
    }
}
