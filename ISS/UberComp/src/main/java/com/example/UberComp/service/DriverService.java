package com.example.UberComp.service;

import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.driver.*;
import com.example.UberComp.dto.user.CreatedUserDTO;
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
        request.setRequestDate(LocalDateTime.now());
        changeRequestRepository.save(request);
    }

    List<Driver> findByStatus(DriverStatus status){
        return driverRepository.findByStatus(status);
    }
}