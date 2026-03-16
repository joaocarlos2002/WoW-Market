package com.wowmarket.wowmarket.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class BlizzardTokenDtoTest {

    @Test
    void factorySetsObtainedAtAndNotExpired() {
        BlizzardTokenDto dto = BlizzardTokenDto.of("token-value", "bearer", 300);
        long now = Instant.now().getEpochSecond();

        assertNotNull(dto.getAccessToken());
        assertEquals("token-value", dto.getAccessToken());
        assertEquals("bearer", dto.getTokenType());
        assertTrue(dto.getObtainedAtEpochSec() <= now && dto.getObtainedAtEpochSec() >= now - 2);
        assertFalse(dto.isExpired());
    }

    @Test
    void expiredWhenPastExpiresIn() {
        long expiresIn = 10L;
        long obtainedAt = Instant.now().getEpochSecond() - (expiresIn + 5);
        BlizzardTokenDto dto = new BlizzardTokenDto("t", "b", expiresIn, obtainedAt);

        assertTrue(dto.isExpired(0));
        assertTrue(dto.isExpired());
    }

    @Test
    void marginCausesEarlyExpiration() {
        long expiresIn = 120L;
        long obtainedAt = Instant.now().getEpochSecond() - (expiresIn - 30); // 30s left
        BlizzardTokenDto dto = new BlizzardTokenDto("t", "b", expiresIn, obtainedAt);

        // margin 60s => should be considered expired because only 30s left
        assertTrue(dto.isExpired(60));
        // smaller margin should not mark as expired
        assertFalse(dto.isExpired(10));
    }
}

