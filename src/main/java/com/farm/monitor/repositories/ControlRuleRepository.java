package com.farm.monitor.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farm.monitor.entities.ControlRule;
import com.farm.monitor.enums.Parameter;

import java.util.List;

@Repository
public interface ControlRuleRepository extends JpaRepository<ControlRule, Long> {
    List<ControlRule> findByParameterAndNode_DevEUIAndIsActive(Parameter parameter, String devEUI, boolean isActive);
}
