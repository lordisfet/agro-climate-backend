package com.farm.monitor.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farm.monitor.dto.MeasurementDTO;
import com.farm.monitor.entities.Measurement;
import com.farm.monitor.repositories.MeasurementRepository;
import com.farm.monitor.repositories.NodeRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MeasurementService {
    private final MeasurementRepository measurementRepository;
    private final NodeRepository nodeRepository; 

    @Transactional
    public Measurement createMeasurement(MeasurementDTO measurementDTO) {
        var node = nodeRepository.findByDevEUI(measurementDTO.getDevEUI().toUpperCase()).orElseThrow(() -> new IllegalArgumentException("Node not found for DevEUI: " + measurementDTO.getDevEUI()));

        node.setLastUpdate(measurementDTO.getGatewayTime());
        var measurement = new Measurement(measurementDTO, node);
        measurementRepository.save(measurement);

        return measurement;
    } 
}
