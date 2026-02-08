package com.example.UberComp.service;

import com.example.UberComp.dto.report.CreateReportDTO;
import com.example.UberComp.dto.report.CreatedReportDTO;
import com.example.UberComp.dto.report.GetReportDTO;
import com.example.UberComp.model.Report;
import com.example.UberComp.model.Ride;
import com.example.UberComp.model.User;
import com.example.UberComp.repository.ReportRepository;
import com.example.UberComp.repository.RideRepository;
import com.example.UberComp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@AllArgsConstructor
public class ReportService {
    @Autowired
    public ReportRepository reportRepository;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public RideRepository rideRepository;

    public CreatedReportDTO createReport(@RequestBody CreateReportDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ride ride = rideRepository.findById(dto.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        Report saved = reportRepository.save(new Report(dto, user, ride));

        return new CreatedReportDTO(saved);
    }

    public List<GetReportDTO> getReportsForRide(Long rideID){
        List<Report> rawReports = reportRepository.findAllByRideId(rideID);
        List<GetReportDTO> reports = rawReports.stream().map(GetReportDTO::new).toList();
        return reports;
    }

}
