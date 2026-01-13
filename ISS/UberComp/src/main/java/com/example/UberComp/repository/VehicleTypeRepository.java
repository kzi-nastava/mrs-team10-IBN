package com.example.UberComp.repository;

import com.example.UberComp.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {
    VehicleType findVehicleTypeByName(String name);
}
