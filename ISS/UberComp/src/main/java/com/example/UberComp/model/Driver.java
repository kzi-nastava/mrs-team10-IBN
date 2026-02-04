package com.example.UberComp.model;

import com.example.UberComp.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@DiscriminatorValue("Driver")
@ToString(exclude = {"vehicle"})
@EqualsAndHashCode(exclude = {"vehicle"}, callSuper = true)
public class Driver extends User {

    @Enumerated(EnumType.STRING)
    private DriverStatus status;

    private Integer uptime;

    @OneToOne(mappedBy = "driver")
    private Vehicle vehicle;

    @Column(name = "daily_work_start")
    private LocalDateTime dailyWorkStart;

    @Column(name = "total_work_minutes_today")
    private Integer totalWorkMinutesToday = 0;

    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;
}


