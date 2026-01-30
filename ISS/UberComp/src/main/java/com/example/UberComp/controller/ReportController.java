package com.example.UberComp.controller;

import com.example.UberComp.dto.report.CreateReportDTO;
import com.example.UberComp.dto.report.CreatedReportDTO;
import com.example.UberComp.dto.report.GetReportDTO;
import com.example.UberComp.model.Account;
import com.example.UberComp.service.ReportService;
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
@RequestMapping("api/reports")
public class ReportController {

    private ReportService reportService;

    @PreAuthorize("hasAuthority('passenger')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedReportDTO> createReport(@RequestBody CreateReportDTO createReportDTO, Authentication auth) {
        Account acc = (Account) auth.getPrincipal();
        createReportDTO.setUserId(acc.getUser().getId());
        CreatedReportDTO createdReport = reportService.createReport(createReportDTO);
        return new ResponseEntity<>(createdReport,HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('passenger', 'driver', 'administrator')")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GetReportDTO>> getReports(@PathVariable Long id){
        List<GetReportDTO> reports = reportService.getReportsForRide(id);
        return ResponseEntity.ok(reports);
    }
}
