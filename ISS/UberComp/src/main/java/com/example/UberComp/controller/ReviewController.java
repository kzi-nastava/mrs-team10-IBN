package com.example.UberComp.controller;

import com.example.UberComp.dto.review.CreateReviewDTO;
import com.example.UberComp.dto.review.CreatedReviewDTO;
import com.example.UberComp.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private ReviewService reviewService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedReviewDTO> createReview(@RequestBody CreateReviewDTO createReviewDTO) {
        CreatedReviewDTO savedReview = reviewService.createReview(createReviewDTO);
        return new ResponseEntity<CreatedReviewDTO>(savedReview, HttpStatus.CREATED);
    }

}
