package com.example.UberComp.service;

import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.driver.GetVehiclePositionDTO;
import com.example.UberComp.dto.driver.CreateDriverDTO;
import com.example.UberComp.dto.driver.DriverDTO;
import com.example.UberComp.dto.driver.VehicleDTO;
import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.dto.user.CreatedUserDTO;
import com.example.UberComp.model.Account;
import com.example.UberComp.model.Driver;
import com.example.UberComp.model.User;
import com.example.UberComp.model.Vehicle;
import com.example.UberComp.repository.DriverRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DriverService {
    @Autowired
    DriverRepository driverRepository;

    public List<GetVehiclePositionDTO> getVehiclePositions(){
        return java.util.List.of();
    }
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
}
