package com.example.UberComp.controller;

import com.example.UberComp.dto.account.UpdateAccountDTO;
import com.example.UberComp.dto.account.UpdatedAccountDTO;
import com.example.UberComp.dto.report.CreatedReportDTO;
import com.example.UberComp.dto.ride.GetRideDTO;
import com.example.UberComp.dto.vehicle.GetVehicleTypeDTO;
import com.example.UberComp.dto.vehicle.UpdateVehicleTypeDTO;
import com.example.UberComp.service.VehicleTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/prices")
public class VehicleTypeController {
    private VehicleTypeService vehicleTypeService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetVehicleTypeDTO>> getVehicleTypes(){
        Collection<GetVehicleTypeDTO> vehicleTypes = vehicleTypeService.getVehicleTypes();
        return ResponseEntity.ok(vehicleTypes);
    }


    @PreAuthorize("hasAuthority('admin')")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<Collection<UpdateVehicleTypeDTO>> updateVehicleType(@RequestBody Collection<UpdateVehicleTypeDTO> toUpdate){
        vehicleTypeService.saveVehicleTypes(toUpdate);
        return ResponseEntity.ok(toUpdate);
    }
}
