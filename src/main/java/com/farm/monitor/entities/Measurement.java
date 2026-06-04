package com.farm.monitor.entities;

import java.time.Instant;

import com.farm.monitor.dto.MeasurementDTO;

import jakarta.persistence.Index;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "measurements", 
    indexes = 
        @Index(name = "idx_measurements_node_id", columnList = "node_id, timestamp DESC"), 
    check = @CheckConstraint(name = "valid_measurement", constraint = 
        "battery_level IS NOT NULL OR temperature IS NOT NULL OR humidity IS NOT NULL OR co2_level IS NOT NULL"))
@Getter
@Setter
@NoArgsConstructor
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private Long id; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", nullable = false, foreignKey = @ForeignKey(name = "fk_measurements_node_id"))
    private Node node;

    @Column(name = "battery_level", nullable = true, check = 
    @CheckConstraint(name = "battery_level_check", constraint = "battery_level >= 0 AND battery_level <= 100"))
    private Integer batteryLevel;

    @Column(name = "temperature", nullable = true, check = 
    @CheckConstraint(name = "temperature_check", constraint = "temperature >= -273.15"))
    private Double temperature;
    
    @Column(name = "humidity", nullable = true, check = 
    @CheckConstraint(name = "humidity_check", constraint = "humidity >= 0 AND humidity <= 100"))
    private Double humidity;
    
    @Column(name = "co2_level", nullable = true, check = 
    @CheckConstraint(name = "co2_level_check", constraint = "co2_level >= 0"))
    private Double co2Level;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    public Measurement(MeasurementDTO dto, Node node) {
        this.node = node;
        this.batteryLevel = dto.getBatteryLevel();
        this.temperature = dto.getTemperature();
        this.humidity = dto.getHumidity();
        this.co2Level = dto.getCo2Level()  ;
        this.timestamp = dto.getGatewayTime();
    }
}
