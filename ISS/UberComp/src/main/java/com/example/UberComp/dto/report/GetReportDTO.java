package com.example.UberComp.dto.report;

import com.example.UberComp.model.Report;
import lombok.Data;

@Data
public class GetReportDTO {
    private Long id;
    private String username;
    private String content;

    public GetReportDTO(Report report){
        this.id = report.getId();
        this.username = report.getUser().getName() + " " + report.getUser().getLastName();
        this.content = report.getContent();
    }
}
