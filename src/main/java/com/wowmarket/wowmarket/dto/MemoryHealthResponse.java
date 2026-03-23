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
        if (memoryHealth != null) {
            this.status = (String) memoryHealth.get("status");
            this.freeMemory = ((Number) memoryHealth.get("freeMemory")).longValue();
            this.totalMemory = ((Number) memoryHealth.get("totalMemory")).longValue();
            this.usedMemory = ((Number) memoryHealth.get("usedMemory")).longValue();
            this.maxMemory = ((Number) memoryHealth.get("maxMemory")).longValue();
        }
    }
}
