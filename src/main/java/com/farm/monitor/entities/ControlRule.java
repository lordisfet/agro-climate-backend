package com.farm.monitor.entities;

import com.farm.monitor.enums.Parameter;
import com.farm.monitor.enums.Level;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "control_rules", check = {
    @CheckConstraint(name = "values_exist", constraint = "min_value IS NOT NULL OR max_value IS NOT NULL"),
    @CheckConstraint(name = "min_max_check", constraint = "(min_value IS NULL OR max_value IS NULL) OR (min_value <= max_value)")
})
@Getter
@Setter
@NoArgsConstructor
public class ControlRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "parameter", nullable = false)
    private Parameter parameter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", nullable = false, foreignKey = @ForeignKey(name = "fk_control_rules_node_id"))
    private Node node;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private Level level;

    @Column(name = "min_value", nullable = true)
    private Double minValue;

    @Column(name = "max_value", nullable = true)
    private Double maxValue;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
