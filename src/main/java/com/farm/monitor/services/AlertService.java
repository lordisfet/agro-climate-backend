package com.farm.monitor.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.farm.monitor.entities.ControlRule;
import com.farm.monitor.entities.Measurement;
import com.farm.monitor.repositories.AlertRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    
    public void createAlert(Measurement measurement, List<ControlRule> rules) {
        if (rules.isEmpty()) {
            return;
        }

        // logic for creating alert...
    }
}
