package com.farm.monitor.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farm.monitor.dto.MeasurementDTO;
import com.farm.monitor.services.MeasurementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/measurements")
@RequiredArgsConstructor
public class MeasurementController {
    private static final Logger logger = LoggerFactory.getLogger(MeasurementController.class);
    private final MeasurementService measurementService;

    @GetMapping
    public ResponseEntity<List<MeasurementDTO>> getMeasurements() {
        List<MeasurementDTO> measurements = measurementService.getAllMeasurements();
        return ResponseEntity.ok(measurements);
    }

    @PostMapping
    public ResponseEntity<Void> receiveMeasurement(@Valid @RequestBody MeasurementDTO measurementDTO) {
        logger.info("Received measurement: {}", measurementDTO);
        try {
            measurementService.createMeasurement(measurementDTO);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Error processing measurement: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
