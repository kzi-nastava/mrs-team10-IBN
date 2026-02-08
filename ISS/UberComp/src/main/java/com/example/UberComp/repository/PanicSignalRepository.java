package com.example.UberComp.repository;

import com.example.UberComp.model.PanicSignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PanicSignalRepository extends JpaRepository<PanicSignal, Long> {
    PanicSignal getById(Long id);
}
