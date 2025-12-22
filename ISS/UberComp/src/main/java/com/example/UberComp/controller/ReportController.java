package com.example.UberComp.controller;

import com.example.UberComp.dto.report.CreateReportDTO;
import com.example.UberComp.dto.report.CreatedReportDTO;
import com.example.UberComp.service.ReportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/reports")
public class ReportController {

    private ReportService reportService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedReportDTO> createReport(@RequestBody CreateReportDTO createReportDTO) {
        CreatedReportDTO createdReport = reportService.createReport(createReportDTO);
        return ResponseEntity.ok(createdReport);
    }
}
