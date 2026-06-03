package com.farm.monitor.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class MeasurementDTO {
    @NotBlank
    @JsonProperty("devEUI")
    private String devEUI;

    @NotBlank
    @JsonProperty("deviceName")
    private String deviceName;

    @JsonProperty("battery")
    @Min(value = 0)
    @Max(value = 100)
    private Integer batteryLevel;

    @JsonProperty("temperature")
    @Min(value = -100)
    @Max(value = 100)
    private Double temperature;

    @JsonProperty("humidity")
    @Min(value = 0)
    @Max(value = 100)
    private Double humidity;

    @JsonProperty("gatewayTime")
    private Instant gatewayTime;
}
