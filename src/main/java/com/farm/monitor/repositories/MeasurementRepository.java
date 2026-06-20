package com.farm.monitor.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.farm.monitor.dto.projections.MeasurementAggregation;
import com.farm.monitor.entities.Measurement;
import java.time.Instant;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    List<Measurement> findByNode_DevEUI(String devEUI);
    List<Measurement> findByNode_DevEUIAndTimestampBetweenOrderByTimestampAsc(String devEUI, Instant startDate, Instant endDate);

    @Query(value = "SELECT " +
            "date_trunc('hour', m.timestamp) AS timestamp, " +
            "AVG(m.temperature) AS temperature, " +
            "AVG(m.humidity) AS humidity " +
            "FROM measurements m " +
            "JOIN nodes n ON m.node_id = n.id " +
            "WHERE n.dev_eui = :devEUI AND m.timestamp BETWEEN :startDate AND :endDate " +
            "GROUP BY date_trunc('hour', m.timestamp) " +
            "ORDER BY timestamp ASC", 
            nativeQuery = true)
    List<MeasurementAggregation> findAggregatedByHour(
            @Param("devEUI") String devEUI, 
            @Param("startDate") Instant startDate, 
            @Param("endDate") Instant endDate);

    @Query(value = "SELECT " +
            "date_trunc('day', m.timestamp) AS timestamp, " +
            "AVG(m.temperature) AS temperature, " +
            "AVG(m.humidity) AS humidity " +
            "FROM measurements m " +
            "JOIN nodes n ON m.node_id = n.id " +
            "WHERE n.dev_eui = :devEUI AND m.timestamp BETWEEN :startDate AND :endDate " +
            "GROUP BY date_trunc('day', m.timestamp) " +
            "ORDER BY timestamp ASC", 
            nativeQuery = true)
    List<MeasurementAggregation> findAggregatedByDay(
            @Param("devEUI") String devEUI, 
            @Param("startDate") Instant startDate, 
            @Param("endDate") Instant endDate);
}
