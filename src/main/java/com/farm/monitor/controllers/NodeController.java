package com.farm.monitor.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farm.monitor.dto.NodeDTO;
import com.farm.monitor.services.NodeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/nodes")
@RequiredArgsConstructor
public class NodeController {
    private final NodeService nodeService;

    @GetMapping
    public ResponseEntity<List<NodeDTO>> getNodes() {
        List<NodeDTO> nodes = nodeService.getAllNodes();
        return ResponseEntity.ok(nodes);
    }
}
