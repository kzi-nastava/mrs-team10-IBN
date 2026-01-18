package com.example.UberComp.model;

import com.example.UberComp.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.*;

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
}


