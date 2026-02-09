package com.example.UberComp.dto.driver;

import com.example.UberComp.model.Coordinate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCoordinateDTO {

    private Long id;

    @NotNull(message = "Latitude is required")
    private Double lat;

    @NotNull(message = "Longitude is required")
    private Double lon;

    @NotBlank(message = "Address is required")
    private String address;

    public GetCoordinateDTO(Coordinate coordinate) {
        this.id = coordinate.getId();
        this.lat = coordinate.getLat();
        this.lon = coordinate.getLon();
        this.address = coordinate.getAddress();
    }
}