package com.example.UberComp.model;

import com.example.UberComp.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@DiscriminatorValue("Driver")
public class Driver extends User {

    @Enumerated(EnumType.STRING)
    private DriverStatus status;

    private Integer uptime;

    @OneToOne(mappedBy = "driver")
    private Vehicle vehicle;
}

