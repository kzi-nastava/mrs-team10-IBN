package com.example.UberComp.repository;

import com.example.UberComp.enums.DriverStatus;
import com.example.UberComp.model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findById(Long id);

    List<Driver> findByStatus(DriverStatus status);
    Page<Driver> findByStatus(DriverStatus status, Pageable pageable);
    List<Driver> findAll();
}
