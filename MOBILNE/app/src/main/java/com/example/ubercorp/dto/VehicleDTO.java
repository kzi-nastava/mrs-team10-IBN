package com.example.ubercorp.dto;

public class VehicleDTO {
    private VehicleTypeDTO vehicleTypeDTO;
    private String model;
    private String plate;
    private Integer seatNumber;
    private Boolean babySeat;
    private Boolean petFriendly;

    public VehicleDTO() {}

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
    public void setVehicleTypeDTO(VehicleTypeDTO vehicleTypeDTO) {
        this.vehicleTypeDTO = vehicleTypeDTO;
    }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public Integer getSeatNumber() { return seatNumber; }
    public void setSeatNumber(Integer seatNumber) { this.seatNumber = seatNumber; }

    public Boolean getBabySeat() { return babySeat; }
    public void setBabySeat(Boolean babySeat) { this.babySeat = babySeat; }

    public Boolean getPetFriendly() { return petFriendly; }
    public void setPetFriendly(Boolean petFriendly) { this.petFriendly = petFriendly; }
}