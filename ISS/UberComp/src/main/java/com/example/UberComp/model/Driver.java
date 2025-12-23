package com.example.UberComp.model;

import com.example.UberComp.enums.DriverStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Driver extends User {
    private DriverStatus status;
    private Integer uptime;
    private Vehicle vehicle;
}
