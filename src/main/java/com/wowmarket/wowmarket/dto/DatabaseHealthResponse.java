package com.wowmarket.wowmarket.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DatabaseHealthResponse {

    private String status;
    private int activeConnections;
    private int idleConnections;
    private int totalConnections;
    private int threadsAwaitingConnection;

    public DatabaseHealthResponse(Map<String, Object> databaseHealth) {
        if (databaseHealth != null) {
            this.status = (String) databaseHealth.get("status");
            this.activeConnections = ((Number) databaseHealth.get("activeConnections")).intValue();
            this.idleConnections = ((Number) databaseHealth.get("idleConnections")).intValue();
            this.totalConnections = ((Number) databaseHealth.get("totalConnections")).intValue();
            this.threadsAwaitingConnection = ((Number) databaseHealth.get("threadsAwaitingConnection")).intValue();
        }
    }
}
