package com.farm.monitor.controllers;

import com.farm.monitor.repositories.NodeRepository;
import java.lang.foreign.Linker.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        @PathVariable("devEUI") String devEUI,
        @RequestParam(required = false) String param
    ) {
        if (nodeService.exists(devEUI)) {
            List<MeasurementDTO> measurements = measurementService.getMeasurementsByDevEUI(devEUI);
            return ResponseEntity.ok(measurements);
        }
        return ResponseEntity.notFound().build();
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
