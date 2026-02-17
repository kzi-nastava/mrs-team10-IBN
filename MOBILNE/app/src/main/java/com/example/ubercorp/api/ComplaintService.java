package com.example.ubercorp.api;

import com.example.ubercorp.dto.CreateReportDTO;
import com.example.ubercorp.dto.CreateReviewDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ComplaintService {
    @POST("api/reports")
    Call<CreateReportDTO> createReport(@Header("Authorization") String authToken,
                                       @Body CreateReportDTO dto);
}

