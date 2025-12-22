package com.example.UberComp.service;

import com.example.UberComp.dto.report.CreateReportDTO;
import com.example.UberComp.dto.report.CreatedReportDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
public class ReportService {
    public CreatedReportDTO createReport(@RequestBody CreateReportDTO createReportDTO) {
        return new CreatedReportDTO();
    }
}
