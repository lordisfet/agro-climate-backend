package com.farm.monitor.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farm.monitor.entities.Alert;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long>{
    
}
