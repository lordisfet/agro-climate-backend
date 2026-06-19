package com.farm.monitor.dto;

import java.time.Instant;

import com.farm.monitor.entities.Node;
import com.farm.monitor.enums.Location;
import com.farm.monitor.enums.Status;

public record NodeDTO(
    String devEUI,
    Status status,
    Location location,
    Instant lastUpdate
) 
{
    public NodeDTO(Node node) {
        this(node.getDevEUI(), node.getStatus(), node.getLocation(), node.getLastUpdate());
    }
}
 