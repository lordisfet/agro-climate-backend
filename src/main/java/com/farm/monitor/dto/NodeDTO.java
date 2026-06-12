package com.farm.monitor.dto;

import java.time.Instant;

import com.farm.monitor.entities.Node;
import com.farm.monitor.enums.Location;
import com.farm.monitor.enums.Status;

import jakarta.persistence.CheckConstraint;

public record NodeDTO(
    Long devEUI,
    Status status,
    Location location,
    Instant lastUpdate
) 
{
    public NodeDTO() {
        this(null, null, null, null);
    }
    public NodeDTO(Node node) {
        this(node.getDevEUI(), node.getStatus(), node.getLocation(), node.getLastUpdate());
    }
}
