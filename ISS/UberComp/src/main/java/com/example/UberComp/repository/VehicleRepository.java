package com.example.UberComp.repository;

import com.example.UberComp.model.Vehicle;
import com.example.UberComp.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Vehicle findByPlate(String plate);
}