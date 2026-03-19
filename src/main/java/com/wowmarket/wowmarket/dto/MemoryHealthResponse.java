package com.wowmarket.wowmarket.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MemoryHealthResponse {

    private String status;
    private long totalMemory;
    private long freeMemory;
    private long usedMemory;
    private long maxMemory;

    public MemoryHealthResponse(Map<String, Object> memoryHealth) {
        if ( memoryHealth != null) {
        this.status = (String) memoryHealth.get("status");
        this.freeMemory = (long) memoryHealth.get("freeMemory");
        this.totalMemory = (long) memoryHealth.get("totalMemory");
        this.usedMemory = (long) memoryHealth.get("usedMemory");
        this.maxMemory = (long) memoryHealth.get("maxMemory");
        }
    }
}
