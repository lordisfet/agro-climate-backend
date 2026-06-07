package com.farm.monitor.services;

import java.time.Instant;

import java.util.List;

import org.springframework.stereotype.Service;

import com.farm.monitor.entities.Alert;
import com.farm.monitor.entities.ControlRule;
import com.farm.monitor.entities.Measurement;
import com.farm.monitor.notifications.AlertMessageGenerator;
import com.farm.monitor.notifications.TelegramBotService;
import com.farm.monitor.repositories.AlertRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    private final AlertMessageGenerator alertMessageGenerator;
    private final TelegramBotService telegramBotService;
    
    @Transactional
    public void createAlert(Measurement measurement, List<ControlRule> rules) {
        if (rules.isEmpty()) {
            return;
        }

        String message = alertMessageGenerator.messageGenerate(measurement, rules);

        Alert alert = new Alert();
        alert.setMeasurement(measurement);
        alert.setViolatedRules(rules);
        alert.setMessage(message);
        alert.setCreatedAt(Instant.now());

        alertRepository.save(alert);

        telegramBotService.sendAlert(message);
    }
}
