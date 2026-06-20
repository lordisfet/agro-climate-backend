package com.farm.monitor.dto.projections;

import java.time.Instant;

public interface MeasurementAggregation {
    Instant getTimestamp();
    Double getTemperature();
    Double getHumidity();
}
