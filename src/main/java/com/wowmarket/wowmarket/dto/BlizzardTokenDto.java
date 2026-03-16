package com.wowmarket.wowmarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.annotation.PostConstruct;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BlizzardTokenDto {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private long expiresIn;

    @JsonIgnore
    private long obtainedAtEpochSec;

    @JsonAnySetter
    public void handleUnknownProperty(String name, Object value) {
        // Ignora propriedades desconhecidas
    }

    public BlizzardTokenDto(String accessToken, String tokenType, long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.obtainedAtEpochSec = Instant.now().getEpochSecond();
    }

    public BlizzardTokenDto(String accessToken, String tokenType, long expiresIn, long obtainedAtEpochSec) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.obtainedAtEpochSec = obtainedAtEpochSec;
    }

    public static BlizzardTokenDto of(String accessToken, String tokenType, long expiresIn) {
        return new BlizzardTokenDto(accessToken, tokenType, expiresIn);
    }

    public boolean isExpired() {
        return isExpired(60);
    }

    public boolean isExpired(long marginSec) {
        long currentEpochSec = Instant.now().getEpochSecond();
        return currentEpochSec >= ((obtainedAtEpochSec + expiresIn) - marginSec);
    }
}
