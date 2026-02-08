package com.example.UberComp.service;

import com.example.UberComp.dto.ride.GetRideDTO;
import com.example.UberComp.dto.vehicle.GetVehicleTypeDTO;
import com.example.UberComp.dto.vehicle.UpdateVehicleTypeDTO;
import com.example.UberComp.model.VehicleType;
import com.example.UberComp.repository.VehicleTypeRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@AllArgsConstructor
@RequiredArgsConstructor
@Service
public class VehicleTypeService {
    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    public Collection<GetVehicleTypeDTO> getVehicleTypes(){
        Collection<VehicleType> vehicleTypes = vehicleTypeRepository.findAll();
        return vehicleTypeRepository.findAll()
                .stream()
                .map(GetVehicleTypeDTO::new)
                .toList();
    }

    public void saveVehicleTypes(Collection<UpdateVehicleTypeDTO> toUpdate){
        for(UpdateVehicleTypeDTO update: toUpdate)
            vehicleTypeRepository.save(new VehicleType(update));
    }

}
