package com.example.UberComp.service;

import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.driver.*;
import com.example.UberComp.dto.user.CreatedUserDTO;
import com.example.UberComp.model.*;
import com.example.UberComp.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DriverChangeRequestRepository changeRequestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public List<GetVehiclePositionDTO> getVehiclePositions() {
        return java.util.List.of();
    }

    @Transactional
    public DriverDTO register(CreateDriverDTO dto) {
        return new DriverDTO(
                dto.getAccountDTO(),
                dto.getCreateUserDTO(),
                dto.getVehicleDTO(),
                0
        );
    }

    public DriverDTO findById(Long id) {
        Driver driver = driverRepository.findById(id).orElse(null);
        assert driver != null;
        Account account = driver.getAccount();
        Vehicle vehicle = driver.getVehicle();
        return new DriverDTO(
                new AccountDTO(account.getEmail()),
                new CreatedUserDTO(driver),
                new VehicleDTO(vehicle),
                driver.getUptime()
        );
    }

    public DriverDTO findByUserId(Long userId) {
        Driver driver = driverRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Driver not found with user id: " + userId));

        Account account = driver.getAccount();
        Vehicle vehicle = driver.getVehicle();

        return new DriverDTO(
                new AccountDTO(account.getEmail()),
                new CreatedUserDTO(driver),
                new VehicleDTO(vehicle),
                driver.getUptime()
        );
    }

    @Transactional
    public void submitDriverChangeRequest(Long driverId, UpdateDriverDTO changeRequest) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        DriverChangeRequest request = new DriverChangeRequest();
        request.setDriver(driver);

        try {
            request.setRequestedChanges(objectMapper.writeValueAsString(changeRequest));
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization failed", e);
        }

        request.setStatus("PENDING");
        changeRequestRepository.save(request);
    }

    @Transactional
    public DriverDTO updateDriverProfile(Long driverId, UpdateDriverDTO updatedDriverDTO) {
        Driver driver = driverRepository.findById(driverId).orElse(null);

        if (updatedDriverDTO.getCreateUserDTO() != null) {
            driver.setName(updatedDriverDTO.getCreateUserDTO().getName());
            driver.setLastName(updatedDriverDTO.getCreateUserDTO().getLastName());
            driver.setHomeAddress(updatedDriverDTO.getCreateUserDTO().getHomeAddress());
            driver.setPhone(updatedDriverDTO.getCreateUserDTO().getPhone());
            userRepository.save(driver);
        }

        if (updatedDriverDTO.getVehicleDTO() != null) {
            Vehicle vehicle = driver.getVehicle();
            vehicle.setModel(updatedDriverDTO.getVehicleDTO().getModel());
            VehicleTypeDTO vehicleTypeDTO = updatedDriverDTO.getVehicleDTO().getVehicleTypeDTO();
            VehicleType vehicleType = new VehicleType(
                    vehicleTypeDTO.getId(),
                    vehicleTypeDTO.getName(),
                    vehicleTypeDTO.getPrice()
            );
            vehicle.setVehicleType(vehicleType);
            vehicle.setPlate(updatedDriverDTO.getVehicleDTO().getPlate());
            vehicle.setSeatNumber(updatedDriverDTO.getVehicleDTO().getSeatNumber());
            vehicle.setBabySeat(updatedDriverDTO.getVehicleDTO().getBabySeat());
            vehicle.setPetFriendly(updatedDriverDTO.getVehicleDTO().getPetFriendly());
            vehicleRepository.save(vehicle);
        }

        return new DriverDTO(
                new AccountDTO(driver.getAccount().getEmail()),
                new CreatedUserDTO(driver),
                new VehicleDTO(driver.getVehicle()),
                driver.getUptime()
        );
    }
}