package com.example.UberComp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class Route {
    private Long id;
    private List<Coordinate> stations;
}
