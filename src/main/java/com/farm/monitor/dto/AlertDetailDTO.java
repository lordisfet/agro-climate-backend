package com.farm.monitor.dto;

public record AlertDetailDTO(
        String parameterName, 
        Double actualValue, 
        Double minValue, 
        Double maxValue
    ) {}
