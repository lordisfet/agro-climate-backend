package com.farm.monitor.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farm.monitor.entities.Node;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
    Node findByDevEUI(String devEUI);
}
