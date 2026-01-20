package com.example.UberComp.model;

import com.example.UberComp.dto.report.CreateReportDTO;
import com.example.UberComp.dto.review.CreateReviewDTO;
import com.example.UberComp.dto.review.CreatedReviewDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @ManyToOne
    private User user;

    @ManyToOne
    private Ride ride;

    public Report(CreateReportDTO dto, User user, Ride ride){
        this.content = dto.getContent();
        this.user = user;
        this.ride = ride;
    }
}
