package com.farm.monitor.dto;

import java.time.Instant;

import com.farm.monitor.dto.projections.MeasurementAggregation;
import com.farm.monitor.entities.Measurement;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MeasurementDTO {
    private static final long batteryMin = 0L;
    private static final long batteryMax = 100L;
    private static final long temperatureMin = -100L;
    private static final long temperatureMax = 100L;
    private static final long humidityMin = 0L;
    private static final long humidityMax = 100L;
    private static final long co2Min = 0L;
    private static final long co2Max = 5000L;

    @NotBlank
    @JsonProperty("devEUI")
    private String devEUI;

    // @NotBlank
    // @JsonProperty("deviceName")
    // private String deviceName;

    @JsonProperty("battery")
    @Min(value = batteryMin)
    @Max(value = batteryMax)
    private Double batteryLevel;

    @JsonProperty("temperature")
    @Min(value = temperatureMin)
    @Max(value = temperatureMax)
    private Double temperature;

    @JsonProperty("humidity")
    @Min(value = humidityMin)
    @Max(value = humidityMax)
    private Double humidity;

    @JsonProperty("co2")
    @Min(value = co2Min)
    @Max(value = co2Max)
    private Double co2Level;

    @JsonProperty("gatewayTime")
    private Instant gatewayTime;

    @AssertTrue(message = "At least one of the measurement fields (battery, temperature, humidity, co2) must be provided")
    private boolean isPayloadValid() {
        return batteryLevel != null || temperature != null || humidity != null || co2Level != null; 
    }   
    
    public MeasurementDTO(Measurement measurement) {
        this.devEUI = measurement.getNode().getDevEUI();
        this.batteryLevel = measurement.getBatteryLevel();
        this.temperature = measurement.getTemperature();
        this.humidity = measurement.getHumidity();
        this.co2Level = measurement.getCo2Level();
        this.gatewayTime = measurement.getTimestamp();
    }

    public MeasurementDTO(String devEUI, MeasurementAggregation agg) {
        this.devEUI = devEUI;
        this.gatewayTime = agg.getTimestamp();
        
        this.temperature = agg.getTemperature() != null ? Math.round(agg.getTemperature() * 100.0) / 100.0 : null;
        this.humidity = agg.getHumidity() != null ? Math.round(agg.getHumidity() * 100.0) / 100.0 : null;
    }
}
