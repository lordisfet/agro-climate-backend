package com.farm.monitor.entities;

import java.time.Instant;

import com.farm.monitor.enums.Location;
import com.farm.monitor.enums.Status;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nodes")
@Getter
@Setter
@NoArgsConstructor
public class Node {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Column(name = "dev_eui", nullable = false, unique = true, check = 
    @CheckConstraint(name = "dev_eui_check", constraint = "dev_eui ~ '^[0-9A-Fa-f]{16}$'"))
    private String devEUI;

    @Column(name = "status", nullable = true)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "location", nullable = true)
    @Enumerated(EnumType.STRING)
    private Location location;

    @Column(name = "last_update", nullable = true)
    private Instant lastUpdate;
}
