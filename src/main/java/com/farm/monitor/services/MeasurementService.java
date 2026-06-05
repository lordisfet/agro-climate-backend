package com.farm.monitor.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farm.monitor.dto.MeasurementDTO;
import com.farm.monitor.entities.ControlRule;
import com.farm.monitor.entities.Measurement;
import com.farm.monitor.repositories.ControlRuleRepository;
import com.farm.monitor.repositories.MeasurementRepository;
import com.farm.monitor.repositories.NodeRepository;
import com.farm.monitor.enums.Parameter;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MeasurementService {
    private final MeasurementRepository measurementRepository;
    private final NodeRepository nodeRepository;
    private final ControlRuleRepository controlRuleRepository;

    public List<MeasurementDTO> getAllMeasurements() {
        List<Measurement> measurements = measurementRepository.findAll();
        return measurements.stream().map(MeasurementDTO::new).toList();
    }

    @Transactional
    public Measurement createMeasurement(MeasurementDTO measurementDTO) {
        var node = nodeRepository.findByDevEUI(measurementDTO.getDevEUI().toUpperCase()).orElseThrow(() -> new IllegalArgumentException("Node not found for DevEUI: " + measurementDTO.getDevEUI()));

        node.setLastUpdate(measurementDTO.getGatewayTime());
        var measurement = new Measurement(measurementDTO, node);
        measurementRepository.save(measurement);

        return measurement;
    } 

    private boolean isViolatedRule(Parameter parameter, Double value, String devEUI, boolean isActive) {
        List<ControlRule> controlRules = controlRuleRepository.findByParameterAndIsActive(parameter, devEUI, isActive);
        
        return true; 
    }
}
