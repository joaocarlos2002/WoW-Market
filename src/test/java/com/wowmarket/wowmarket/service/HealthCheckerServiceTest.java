package com.wowmarket.wowmarket.service;

import com.wowmarket.wowmarket.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HealthCheckerServiceTest {

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private HealthCheckerService healthCheckerService;

    @Test
    void testCheckHealthDisk() {
        DiskHealthResponse response = healthCheckerService.checkHealthDisk();

        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertTrue(response.getStatus().equals("UP") || response.getStatus().equals("LOW_SPACE"));
        assertTrue(response.getTotalSpace() > 0);
        assertTrue(response.getFreeSpace() >= 0);
        assertTrue(response.getUsableSpace() >= 0);
    }

    @Test
    void testCheckHealthMemory() {
        MemoryHealthResponse response = healthCheckerService.checkHealthMemory();

        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertTrue(response.getStatus().equals("UP") || response.getStatus().equals("DOWN"));
        assertTrue(response.getTotalMemory() > 0);
        assertTrue(response.getMaxMemory() > 0);
        assertTrue(response.getUsedMemory() >= 0);
    }

    @Test
    void testCheckHealthThreads() {
        ThreadsHealthResponse response = healthCheckerService.checkHealthThreads();

        assertNotNull(response);
        assertNotNull(response.getStatus());
        assertTrue(response.getStatus().equals("UP") || response.getStatus().equals("DOWN"));
        assertTrue(response.getThreadCount() > 0);
        assertTrue(response.getPeakThreadCount() > 0);
        assertTrue(response.getDaemonThreadCount() >= 0);
    }

    @Test
    void testGetCpuUsage() {
        CpuHealthResponse response = healthCheckerService.getCpuUsage();

        assertNotNull(response);
        assertNotNull(response.getStatus());
        // O status pode ser UP, LOW_LOAD, HIGH_LOAD ou não estar definido em alguns sistemas
        assertTrue(response.getStatus() != null && !response.getStatus().isEmpty());
        // systemLoadAverage pode ser -1.0 em alguns sistemas (não suportado)
        assertTrue(response.getCpuUsage() >= -1.0);
    }
}

