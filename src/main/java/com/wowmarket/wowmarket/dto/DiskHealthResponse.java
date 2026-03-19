package com.wowmarket.wowmarket.dto;


import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DiskHealthResponse {

    private String status;
    private long totalSpace;
    private long freeSpace;
    private long usableSpace;

    public DiskHealthResponse(Map<String, Object> diskComponent) {
        this.status = diskComponent.get("status").toString();
        this.totalSpace = (long) diskComponent.get("totalSpace");
        this.freeSpace = (long) diskComponent.get("freeSpace");
        this.usableSpace = (long) diskComponent.get("usableSpace");
    }
}
