package com.wowmarket.wowmarket.service;

import com.wowmarket.wowmarket.dto.*;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.sql.SQLException;

@Service
public class HealthCheckerService {
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckerService.class);

    private final DataSource dataSource;

    public HealthCheckerService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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

    @SuppressWarnings("resource")
    public DatabaseHealthResponse checkHealthDatabase() {
        final HikariDataSource hikari;
        try {
            hikari = dataSource.unwrap(HikariDataSource.class);
        } catch (SQLException e) {
            logger.warn("Nao foi possivel obter HikariDataSource via unwrap", e);
            return DatabaseHealthResponse.builder()
                    .status("UNKNOWN")
                    .activeConnections(0)
                    .idleConnections(0)
                    .totalConnections(0)
                    .threadsAwaitingConnection(0)
                    .build();
        }

        HikariPoolMXBean pool = hikari.getHikariPoolMXBean();
        if (pool == null) {
            logger.warn("Metricas do pool Hikari nao estao disponiveis");
            return DatabaseHealthResponse.builder()
                    .status("UNKNOWN")
                    .activeConnections(0)
                    .idleConnections(0)
                    .totalConnections(0)
                    .threadsAwaitingConnection(0)
                    .build();
        }

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
        double usedMemoryRatio = (double) usedMemory / maxMemory;
        double threshold = 0.9; // 90% of max memory
        boolean memoryUsageHigh = usedMemoryRatio > threshold;


        String statusMemory = memoryUsageHigh ? "DOWN" : "UP";

        if (memoryUsageHigh) {
            logger.warn(
                    "Memória livre baixa: apenas {} bytes livres (uso de memória: {}%)",
                    freeMemory,
                    String.format("%.2f", usedMemoryRatio * 100)
            );
        } else {
            logger.debug(
                    "Memória livre suficiente: {} bytes livres (uso de memória: {}%)",
                    freeMemory,
                    String.format("%.2f", usedMemoryRatio * 100)
            );
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
        OperatingSystemMXBean usageCpu = ManagementFactory.getOperatingSystemMXBean();

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
        DiskHealthResponse diskHealth = checkHealthDisk();
        DatabaseHealthResponse databaseHealth = checkHealthDatabase();
        MemoryHealthResponse memoryHealth = checkHealthMemory();
        ThreadsHealthResponse threadsHealth = checkHealthThreads();
        CpuHealthResponse cpuHealth = getCpuUsage();

        String statusHealth;
        if ("UP".equals(diskHealth.getStatus()) &&
                "UP".equals(databaseHealth.getStatus()) &&
                "UP".equals(memoryHealth.getStatus()) &&
                "UP".equals(threadsHealth.getStatus()) &&
                "UP".equals(cpuHealth.getStatus())) {
            statusHealth = "UP";
        } else {
            statusHealth = "DOWN";
        }

        return HealthResponse.builder()
                .status(statusHealth)
                .checkHealthDisk(diskHealth)
                .databaseHealth(databaseHealth)
                .memoryHealth(memoryHealth)
                .threadsHealth(threadsHealth)
                .cpuHealth(cpuHealth)
                .build();
    }
}
