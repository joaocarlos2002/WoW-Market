package com.wowmarket.wowmarket.dto;


import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ThreadsHealthResponse {

    private String status;
    private int threadCount;
    private int peakThreadCount;
    private int daemonThreadCount;

    public ThreadsHealthResponse(Map<String, Object> ThreadsHealthResponse) {
        this.status = ThreadsHealthResponse.get("status").toString();
        this.threadCount = (int) ThreadsHealthResponse.get("threadCount");
        this.peakThreadCount = (int) ThreadsHealthResponse.get("peakThreadCount");
        this.daemonThreadCount = (int) ThreadsHealthResponse.get("daemonThreadCount");
    }
}
