package com.example.ubercorp.api;

import com.example.ubercorp.dto.CreateReviewDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ReviewService {
    @POST("api/reviews")
    Call<CreateReviewDTO> createReview(@Header("Authorization") String authToken,
                                       @Body CreateReviewDTO dto);
}

