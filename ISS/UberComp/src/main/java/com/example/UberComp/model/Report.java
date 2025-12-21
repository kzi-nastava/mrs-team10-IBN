package com.example.UberComp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Report {
    private Long id;
    private String content;
    private User user;
    private Ride ride;
}
