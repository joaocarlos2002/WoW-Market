package com.wowmarket.wowmarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlizzardTokenDto {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private long expiresIn;

    private long obtainedAtEpochSec;

    public static BlizzardTokenDto of(String accessToken, String tokenType, long expiresIn) {
        return new BlizzardTokenDto(accessToken, tokenType, expiresIn, Instant.now().getEpochSecond());
    }

    public boolean isExpired() {
        return isExpired(60);
    }

    public boolean isExpired(long marginSec) {
        long currentEpochSec = Instant.now().getEpochSecond();
        return currentEpochSec >= ((obtainedAtEpochSec + expiresIn) - marginSec);
    }
}
