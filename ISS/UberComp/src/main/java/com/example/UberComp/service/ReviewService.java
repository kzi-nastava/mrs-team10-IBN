package com.example.UberComp.service;

import com.example.UberComp.dto.review.CreateReviewDTO;
import com.example.UberComp.dto.review.CreatedReviewDTO;
import com.example.UberComp.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class ReviewService {
    private ReviewRepository iReviewRepository;
    public CreatedReviewDTO createReview(CreateReviewDTO createReviewDTO) {
        return new CreatedReviewDTO();
    }
}
