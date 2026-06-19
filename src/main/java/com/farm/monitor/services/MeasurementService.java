package com.farm.monitor.services;

import com.farm.monitor.controllers.MeasurementController;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farm.monitor.dto.MeasurementDTO;
import com.farm.monitor.dto.NodeDTO;
import com.farm.monitor.entities.Measurement;
import com.farm.monitor.repositories.MeasurementRepository;
import com.farm.monitor.repositories.NodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeasurementService {
    private final MeasurementRepository measurementRepository;
    private final NodeRepository nodeRepository;
    private final ControlRuleService controlRuleService;

    public List<MeasurementDTO> getAllMeasurements() {
        List<Measurement> measurements = measurementRepository.findAll();
        return measurements.stream().map(MeasurementDTO::new).toList();
    }

    public List<MeasurementDTO> getMeasurementsByDevEUI(String devEUI) {
        List<Measurement> measurements = measurementRepository.findByNode_DevEUI(devEUI);
        return measurements.stream().map(MeasurementDTO::new).toList();
    }

    public List<MeasurementDTO> getMeasurementsByDevEUIAndDateRange(String devEUI, Instant startDate, Instant endDate) {
        List<Measurement> measurements = measurementRepository.findByNode_DevEUIAndTimestampBetweenOrderByTimestampAsc(devEUI, startDate, endDate);
        return measurements.stream().map(MeasurementDTO::new).toList();
    }

    @Transactional
    public Measurement createMeasurement(MeasurementDTO measurementDTO) {
        var node = nodeRepository.findByDevEUI(measurementDTO.getDevEUI().toUpperCase()).orElseThrow(() -> new IllegalArgumentException("Node not found for DevEUI: " + measurementDTO.getDevEUI()));

        node.setLastUpdate(measurementDTO.getGatewayTime());
        var measurement = new Measurement(measurementDTO, node);
        measurementRepository.save(measurement);
        controlRuleService.checkMeasurement(measurement);

        return measurement;
    } 
}
