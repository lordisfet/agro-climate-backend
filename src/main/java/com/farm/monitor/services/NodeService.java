package com.farm.monitor.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.farm.monitor.dto.NodeDTO;
import com.farm.monitor.entities.Node;
import com.farm.monitor.repositories.NodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NodeService {
    private final NodeRepository nodeRepository;

    public List<NodeDTO> getAllNodes() {
        List<Node> nodes = nodeRepository.findAll();
        return nodes.stream()
        .sorted(Comparator.comparingInt(node -> node.getLocation().getOrderId())).map(NodeDTO::new).toList();
    }

    public Optional<NodeDTO> getNodeByDevEUI(String devEUI) {
        return nodeRepository.findByDevEUI(devEUI).map(NodeDTO::new);
    }

    public boolean exists(String devEUI) {
        return nodeRepository.existsByDevEUI(devEUI);
    }
}
