package com.farm.monitor.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.farm.monitor.entities.Measurement;
import java.time.Instant;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    List<Measurement> findByNode_DevEUI(String devEUI);
    List<Measurement> findByNode_DevEUIAndTimestampBetween(String devEUI, Instant startDate, Instant endDate);
}
