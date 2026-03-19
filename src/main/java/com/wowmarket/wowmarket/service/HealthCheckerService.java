package com.wowmarket.wowmarket.service;

import com.wowmarket.wowmarket.dto.*;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;


@Getter
@Service
public class HealthCheckerService {
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckerService.class);

    private volatile DiskHealthResponse diskHealthResponse;
    private volatile DatabaseHealthResponse databaseHealthResponse;
    private volatile CpuHealthResponse cpuHealthResponse;
    private volatile MemoryHealthResponse memoryHealthResponse;
    private volatile ThreadsHealthResponse threadsHealthResponse;
    private volatile HealthResponse healthResponse;

    @Autowired
    private DataSource dataSource;

    public DiskHealthResponse checkHealthDisk(){
        File disk = new File("/");

        long total = disk.getTotalSpace();
        long free = disk.getFreeSpace();
        long usable = disk.getUsableSpace();

        String status = free > 100_000_000 ? "UP" : "LOW_SPACE";

        if (free <= 100_000_000) {
            logger.warn("Espaço em disco baixo: apenas {} bytes livres", free);
        } else {
            logger.debug("Espaço em disco suficiente: {} bytes livres", free);
        }

        return DiskHealthResponse.builder()
                .status(status)
                .totalSpace(total)
                .freeSpace(free)
                .usableSpace(usable)
                .build();
    }

    public DatabaseHealthResponse checkHealthDatabase() {
        HikariDataSource hikari = (HikariDataSource) dataSource;
        HikariPoolMXBean pool = hikari.getHikariPoolMXBean();

        int activeConnections = pool.getActiveConnections();
        int idleConnections = pool.getIdleConnections();
        int totalConnections = pool.getTotalConnections();
        int threadsAwaitingConnection = pool.getThreadsAwaitingConnection();

        String statusDatabase = (activeConnections < totalConnections) ? "UP" : "DOWN";

        return DatabaseHealthResponse.builder()
                .status(statusDatabase)
                .activeConnections(activeConnections)
                .idleConnections(idleConnections)
                .totalConnections(totalConnections)
                .threadsAwaitingConnection(threadsAwaitingConnection)
                .build();
    }

    public MemoryHealthResponse checkHealthMemory() {

        Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        long  usedMemory = totalMemory - freeMemory;

        String statusMemory = (freeMemory < maxMemory) ? "UP" : "DOWN";

         if (freeMemory < maxMemory) {
            logger.warn("Memória livre baixa: apenas {} bytes livres", freeMemory);
        } else {
            logger.debug("Memória livre suficiente: {} bytes livres", freeMemory);
        }

        return MemoryHealthResponse.builder()
                .status(statusMemory)
                .totalMemory(totalMemory)
                .freeMemory(freeMemory)
                .maxMemory(maxMemory)
                .usedMemory(usedMemory)
                .build();
    }

    public ThreadsHealthResponse checkHealthThreads() {

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        int threadCount  = threadMXBean.getThreadCount();
        int peakThreadCount = threadMXBean.getPeakThreadCount();
        int daemonThreadCount = threadMXBean.getDaemonThreadCount();

        String statusThreads = (threadCount < 200) ? "UP" : "DOWN";

        return ThreadsHealthResponse.builder()
                .status(statusThreads)
                .threadCount(threadCount)
                .peakThreadCount(peakThreadCount)
                .daemonThreadCount(daemonThreadCount)
                .build();
    }

    public CpuHealthResponse getCpuUsage() {

        String statusCpu;
        OperatingSystemMXBean usageCpu =  (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        double systemLoadAverage = usageCpu.getSystemLoadAverage();
        if  (systemLoadAverage < 0.01) {
            statusCpu = "LOW_LOAD";
            logger.warn("Carga de CPU baixa: média de carga do sistema é {}", systemLoadAverage);
        } else if (systemLoadAverage < 0.7) {
            statusCpu = "UP";
            logger.debug("Carga de CPU normal: média de carga do sistema é {}", systemLoadAverage);
        } else {
            statusCpu = "HIGH_LOAD";
            logger.warn("Carga de CPU alta: média de carga do sistema é {}", systemLoadAverage);
        }

        return CpuHealthResponse.builder()
                .cpuUsage(systemLoadAverage)
                .status(statusCpu)
                .build();


    }

    public HealthResponse checkHealth() {
        String statusHealth;

        if(checkHealthDisk().getStatus().equals("UP") &&
                checkHealthDatabase().getStatus().equals("UP") &&
                checkHealthMemory().getStatus().equals("UP") &&
                checkHealthThreads().getStatus().equals("UP") &&
                getCpuUsage().getStatus().equals("UP")) {
            statusHealth = "UP";
        } else {
            statusHealth = "DOWN";
        }

        return HealthResponse.builder()
                .status(statusHealth)
                .checkHealthDisk(checkHealthDisk())
                .databaseHealth(checkHealthDatabase())
                .memoryHealth(checkHealthMemory())
                .threadsHealth(checkHealthThreads())
                .cpuHealth(getCpuUsage())
                .build();
    }
}
