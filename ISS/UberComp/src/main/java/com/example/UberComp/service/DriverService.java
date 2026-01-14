package com.example.UberComp.service;

import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.driver.*;
import com.example.UberComp.dto.user.CreatedUserDTO;
import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.AccountType;
import com.example.UberComp.enums.DriverStatus;
import com.example.UberComp.model.*;
import com.example.UberComp.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverChangeRequestRepository changeRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public List<GetVehiclePositionDTO> getVehiclePositions() {
        return java.util.List.of();
    }

    @Transactional
    public DriverDTO register(CreateDriverDTO dto, User user) {
        if (vehicleRepository.findByPlate(dto.getVehicleDTO().getPlate()) != null) {
            throw new RuntimeException("License plate already registered");
        }

        if (accountRepository.findByEmail(dto.getAccountDTO().getEmail()) == null) {
            throw new RuntimeException("User not found after account creation");
        }

        Driver driver = (Driver) user;
        driver.setUptime(0);
        driver.setStatus(DriverStatus.OFFLINE);

        driver = driverRepository.save(driver);

        Vehicle vehicle = new Vehicle();
        vehicle.setModel(dto.getVehicleDTO().getModel());
        vehicle.setPlate(dto.getVehicleDTO().getPlate());
        vehicle.setSeatNumber(dto.getVehicleDTO().getSeatNumber());
        vehicle.setBabySeat(dto.getVehicleDTO().getBabySeat());
        vehicle.setPetFriendly(dto.getVehicleDTO().getPetFriendly());

        VehicleType vehicleType = vehicleTypeRepository
                .findVehicleTypeByName(dto.getVehicleDTO().getVehicleTypeDTO().getName());
        if (vehicleType != null) {
            vehicle.setVehicleType(vehicleType);
        } else {
            vehicleType = new VehicleType();
            vehicleType.setName(dto.getVehicleDTO().getVehicleTypeDTO().getName());
            vehicleType.setPrice(0.0);
            vehicleType = vehicleTypeRepository.save(vehicleType);
            vehicle.setVehicleType(vehicleType);
        }

        vehicle.setDriver(driver);

        vehicle = vehicleRepository.save(vehicle);

        driver.setVehicle(vehicle);
        driver = driverRepository.save(driver);

        // TODO: activation token i verification mail

        return new DriverDTO(
                new AccountDTO(driver.getAccount().getEmail()),
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
        request.setRequestDate(LocalDateTime.now());
        changeRequestRepository.save(request);
    }

    List<Driver> findByStatus(DriverStatus status){
        return driverRepository.findByStatus(status);
    }
}