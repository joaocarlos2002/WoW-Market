package com.wowmarket.wowmarket.controller;

import com.wowmarket.wowmarket.dto.*;
import com.wowmarket.wowmarket.service.HealthCheckerService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/HealthChecker")
public class HealthCheckerController {

    @Autowired
    private HealthCheckerService healthCheckerService;

    @GetMapping("/")
    public ResponseEntity<@NonNull HealthResponse> checkHealth() {
        HealthResponse health = healthCheckerService.checkHealth();
        return ResponseEntity.ok(health);
    }

    @GetMapping("/disk")
    public ResponseEntity<@NonNull DiskHealthResponse> checkHealthDisk() {
        DiskHealthResponse diskHealth = healthCheckerService.checkHealthDisk();
        return ResponseEntity.ok(diskHealth);
    }

    @GetMapping("/database")
    public ResponseEntity<@NonNull DatabaseHealthResponse> checkHealthDatabase() {
        DatabaseHealthResponse databaseHealth = healthCheckerService.checkHealthDatabase();
        return ResponseEntity.ok(databaseHealth);
    }

    @GetMapping("/memory")
    public ResponseEntity<@NonNull MemoryHealthResponse> checkHealthMemory() {
        MemoryHealthResponse memoryHealth = healthCheckerService.checkHealthMemory();
        return ResponseEntity.ok(memoryHealth);
    }

    @GetMapping("/threads")
    public ResponseEntity<@NonNull ThreadsHealthResponse> checkHealthThreads() {
        ThreadsHealthResponse threadsHealth = healthCheckerService.checkHealthThreads();
        return ResponseEntity.ok(threadsHealth);
    }

    @GetMapping("/cpu")
    public ResponseEntity<@NonNull CpuHealthResponse> getCpuUsage() {
        CpuHealthResponse cpuHealth = healthCheckerService.getCpuUsage();
        return ResponseEntity.ok(cpuHealth);
    }
}
