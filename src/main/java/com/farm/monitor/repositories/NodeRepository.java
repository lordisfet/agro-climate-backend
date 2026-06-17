package com.farm.monitor.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farm.monitor.entities.Node;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
    Optional<Node> findByDevEUI(String devEUI);
    boolean existsByDevEUI(String devEUI);
}
