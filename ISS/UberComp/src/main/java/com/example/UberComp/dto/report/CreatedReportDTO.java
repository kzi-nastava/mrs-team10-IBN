package com.example.UberComp.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatedReportDTO {
    private Long id;
    private String content;
    private Long userId;
    private Long rideId;
}
