package com.example.UberComp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Driver extends User {
    private Boolean isOnline;
    private Boolean isDriving;
    private Integer uptime;
    private Vehicle vehicle;
}
