package com.example.UberComp.service;

import com.example.UberComp.dto.driver.GetVehiclePositionDTO;
import com.example.UberComp.dto.driver.CreateDriverDTO;
import com.example.UberComp.dto.driver.DriverDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DriverService {
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
}
