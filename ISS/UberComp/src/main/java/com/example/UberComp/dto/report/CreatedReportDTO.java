package com.example.UberComp.dto.report;

import com.example.UberComp.model.Report;
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

    public CreatedReportDTO(Report report){
        this.id = report.getId();
        this.content = report.getContent();
        this.userId = report.getUser().getId();
        this.rideId = report.getRide().getId();
    }
}
