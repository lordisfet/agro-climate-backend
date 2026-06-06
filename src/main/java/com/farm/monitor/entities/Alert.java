package com.farm.monitor.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
// Removed RequiredArgsConstructor to avoid generating a duplicate no-args constructor
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_id", nullable = false, foreignKey = @ForeignKey(name = "fk_alerts_measurement_id"))
    private Measurement measurement;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "alert_control_rules",
            joinColumns = @JoinColumn(name = "alert_id"),
            inverseJoinColumns = @JoinColumn(name = "control_rule_id")
    )
    private List<ControlRule> violatedRules;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // @Column(name = "is_resolved", nullable = false)
    // private boolean isResolved = false;
}