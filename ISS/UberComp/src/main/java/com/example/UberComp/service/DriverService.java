package com.example.UberComp.service;

import com.example.UberComp.dto.GetVehiclePositionDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DriverService {
    public List<GetVehiclePositionDTO> getVehiclePosition(){
        return java.util.List.of();
    }
}
