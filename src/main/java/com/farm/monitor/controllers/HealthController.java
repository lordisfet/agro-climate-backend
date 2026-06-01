package com.farm.monitor.controllers; // твой путь может чуть отличаться

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public String checkHealth() {
        return "Poultry Farm Monitor is UP and RUNNING!";
    }
}