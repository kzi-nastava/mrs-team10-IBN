package com.example.UberComp.dto.driver;

import com.example.UberComp.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleTypeDTO {
    private Long id;
    @NotBlank(message = "Vehicle type name is required")
    private String name;

    @NotNull(message = "Price is required")
    private Double price;

    public VehicleTypeDTO(VehicleType vehicleType) {
        this.id = vehicleType.getId();
        this.name = vehicleType.getName();
        this.price = vehicleType.getPrice();
    }
}
