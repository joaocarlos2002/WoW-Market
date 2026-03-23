package com.wowmarket.wowmarket.dto;


import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CpuHealthResponse {

    private String status;
    private double cpuUsage;

    public CpuHealthResponse(Map<String, Object> cpuHealth) {
        if (cpuHealth != null) {
            this.status = (String) cpuHealth.get("status");
            this.cpuUsage = ((Number) cpuHealth.get("cpuUsage")).doubleValue();
        }
    }
}
