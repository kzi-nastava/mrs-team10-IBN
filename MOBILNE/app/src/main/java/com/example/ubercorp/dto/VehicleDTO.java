package com.example.ubercorp.dto;

public class VehicleDTO {
    private VehicleTypeDTO vehicleTypeDTO;
    private String model;
    private String plate;
    private Integer seatNumber;
    private Boolean babySeat;
    private Boolean petFriendly;

    public VehicleDTO(VehicleTypeDTO vehicleTypeDTO, String model, String plate,
                      Integer seatNumber, Boolean babySeat, Boolean petFriendly) {
        this.vehicleTypeDTO = vehicleTypeDTO;
        this.model = model;
        this.plate = plate;
        this.seatNumber = seatNumber;
        this.babySeat = babySeat;
        this.petFriendly = petFriendly;
    }

    public VehicleTypeDTO getVehicleTypeDTO() { return vehicleTypeDTO; }
    public String getModel() { return model; }
    public String getPlate() { return plate; }
    public Integer getSeatNumber() { return seatNumber; }
    public Boolean getBabySeat() { return babySeat; }
    public Boolean getPetFriendly() { return petFriendly; }
}