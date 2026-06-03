package com.farm.monitor.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farm.monitor.entities.Measurement;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    
}
