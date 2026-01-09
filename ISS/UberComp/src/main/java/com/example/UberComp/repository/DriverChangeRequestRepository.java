package com.example.UberComp.repository;

import com.example.UberComp.model.DriverChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverChangeRequestRepository extends JpaRepository<DriverChangeRequest, Long> {
    List<DriverChangeRequest> findByStatus(String status);
}
