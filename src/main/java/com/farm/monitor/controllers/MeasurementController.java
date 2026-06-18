package com.farm.monitor.controllers;

import com.farm.monitor.repositories.NodeRepository;
import java.lang.foreign.Linker.Option;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farm.monitor.dto.MeasurementDTO;
import com.farm.monitor.dto.NodeDTO;
import com.farm.monitor.services.MeasurementService;
import com.farm.monitor.services.NodeService;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.cfg.DateTimeFeature;

@RestController
@RequestMapping("/api/v1/measurements")
@RequiredArgsConstructor
public class MeasurementController {
    private final NodeRepository nodeRepository;
    private final MeasurementService measurementService;
    private final NodeService nodeService;
    private static final Logger logger = LoggerFactory.getLogger(MeasurementController.class);

    @GetMapping
    public ResponseEntity<List<MeasurementDTO>> getMeasurements() {
        List<MeasurementDTO> measurements = measurementService.getAllMeasurements();
        return ResponseEntity.ok(measurements);
    }

    @GetMapping("/node/{devEUI}")
    public ResponseEntity<List<MeasurementDTO>> getMeasurementsByDevEUI(
        @PathVariable("devEUI") 
        String devEUI,

        @RequestParam(value = "startDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
        Instant startDate,

        @RequestParam(value = "endDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
        Instant endDate
    ) {
        if (!nodeService.exists(devEUI)) {
            return ResponseEntity.notFound().build();
        }

        List<MeasurementDTO> measurements;
        if (startDate != null || endDate != null) {
            if (startDate == null) {
                startDate = Instant.now();
            }
            if (endDate == null) {
                endDate = Instant.EPOCH;
            }
            measurements = measurementService.getMeasurementsByDevEUIAndDateRange(devEUI, startDate, endDate);
        }
        else {
            measurements = measurementService.getMeasurementsByDevEUI(devEUI);
        }

        return ResponseEntity.ok(measurements);
    }

    // @GetMapping("/node/{devEUI}?startDate=&{endDate}")
    // public ResponseEntity<List<MeasurementDTO>> getMethodName(@RequestParam Instant startDate, @RequestParam Instant endDate) {

    //     return null;
    // }
    
    // public ResponseEntity<List<MeasurementDTO>> getMeasurementsByTimestampBetween() {

    // }

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
