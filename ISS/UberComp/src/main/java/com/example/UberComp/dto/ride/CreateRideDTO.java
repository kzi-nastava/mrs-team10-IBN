package com.example.UberComp.dto.ride;

import com.example.UberComp.dto.driver.GetCoordinateDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRideDTO {

    @NotNull(message = "Start address is required")
    @Valid
    private GetCoordinateDTO startAddress;

    @NotNull(message = "Destination address is required")
    @Valid
    private GetCoordinateDTO destinationAddress;

    @NotNull(message = "Distance is required")
    @Positive(message = "Distance must be positive")
    private Double distance;

    private List<GetCoordinateDTO> stops;

    private List<@NotBlank(message = "Passenger email cannot be blank")
    @Email(message = "Invalid email format") String> passengerEmails;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    @NotNull(message = "Baby seat field is required")
    private Boolean babySeat;

    @NotNull(message = "Pet friendly field is required")
    private Boolean petFriendly;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduled;

    @AssertTrue(message = "Scheduled time must be within 5 hours from now")
    private boolean isScheduledTimeValid() {
        if (scheduled == null) {
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maxTime = now.plusHours(5);
        return scheduled.isAfter(now) && scheduled.isBefore(maxTime);
    }

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be zero or positive")
    private Double price;

    @NotNull(message = "Estimated duration is required")
    @Positive(message = "Estimated duration must be positive")
    private int estimatedDuration;
}