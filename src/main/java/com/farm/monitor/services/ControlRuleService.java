package com.farm.monitor.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.farm.monitor.entities.ControlRule;
import com.farm.monitor.entities.Measurement;
import com.farm.monitor.enums.Parameter;
import com.farm.monitor.repositories.ControlRuleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ControlRuleService {
    private final ControlRuleRepository controlRuleRepository;
    private final AlertService alertService;

    public void checkMeasurement(Measurement measurement) {
        List<ControlRule> violatedControlRules = new ArrayList<>();
        String devEUI = measurement.getNode().getDevEUI();

        violatedControlRules.addAll(isViolatedRule(Parameter.BATTERY_LEVEL, measurement.getBatteryLevel(), devEUI));
        violatedControlRules.addAll(isViolatedRule(Parameter.TEMPERATURE, measurement.getTemperature(), devEUI));
        violatedControlRules.addAll(isViolatedRule(Parameter.HUMIDITY, measurement.getHumidity(), devEUI));

        alertService.createAlert(measurement, violatedControlRules);
    }

    private List<ControlRule> isViolatedRule(Parameter parameter, Double value ,String devEUI) {
        List<ControlRule> controlRules = controlRuleRepository.findByParameterAndDevEUIAndIsActive(parameter, devEUI, true); 
        List<ControlRule> violatedRules = new ArrayList<>();

        for (ControlRule controlRule : controlRules) {
            if (value > controlRule.getMaxValue() || value < controlRule.getMinValue()) {
                violatedRules.add(controlRule);
            }
        }

        return violatedRules;
    }
}
