package com.example.UberComp.controller;

import com.example.UberComp.dto.review.CreateReviewDTO;
import com.example.UberComp.dto.review.CreatedReviewDTO;
import com.example.UberComp.dto.review.GetReviewDTO;
import com.example.UberComp.model.Account;
import com.example.UberComp.model.Review;
import com.example.UberComp.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private ReviewService reviewService;

    @PreAuthorize("hasAuthority('passenger')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedReviewDTO> createReview(Authentication auth,@Valid @RequestBody CreateReviewDTO createReviewDTO) {
        Account acc = (Account) auth.getPrincipal();
        createReviewDTO.setUserId(acc.getUser().getId());
        CreatedReviewDTO savedReview = reviewService.createReview(createReviewDTO);
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('passenger', 'driver', 'administrator')")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GetReviewDTO>> getReviews(@PathVariable Long id){
        List<GetReviewDTO> reviews = reviewService.getReviewsForRide(id);
        return ResponseEntity.ok(reviews);
    }
}
