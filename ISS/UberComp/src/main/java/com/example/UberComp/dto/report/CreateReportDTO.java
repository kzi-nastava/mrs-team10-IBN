package com.example.UberComp.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateReportDTO {
    private String content;
    private Long userId;
    private Long rideId;
}
