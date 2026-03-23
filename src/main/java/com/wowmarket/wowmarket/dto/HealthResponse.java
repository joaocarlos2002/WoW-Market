package com.wowmarket.wowmarket.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class HealthResponse {

    @NonNull
    private String status;

    private DiskHealthResponse checkHealthDisk;
    private DatabaseHealthResponse databaseHealth;
    private MemoryHealthResponse memoryHealth;
    private ThreadsHealthResponse threadsHealth;
    private CpuHealthResponse cpuHealth;

    public HealthResponse(Map<String, Object> healthData) {
        if (healthData != null) {
            this.status = (String) healthData.get("status");
            this.checkHealthDisk = (DiskHealthResponse) healthData.get("checkHealthDisk");
            this.databaseHealth = (DatabaseHealthResponse) healthData.get("databaseHealth");
            this.memoryHealth = (MemoryHealthResponse) healthData.get("memoryHealth");
            this.threadsHealth = (ThreadsHealthResponse) healthData.get("threadsHealth");
            this.cpuHealth = (CpuHealthResponse) healthData.get("cpuHealth");
        }
    }
}
