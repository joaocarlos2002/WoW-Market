package com.wowmarket.wowmarket.service;

import com.wowmarket.wowmarket.dto.BlizzardTokenDto;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Setter
@Getter
@Service
public class BlizzardAuthService {
    private static final Logger logger = LoggerFactory.getLogger(BlizzardAuthService.class);

    @Value("${blizzard.client-id:}")
    private String clientId;

    @Value("${blizzard.client-secret:}")
    private String clientSecret;

    private final WebClient blizzardAuthWebClient;
    private BlizzardTokenDto cachedToken;

    public BlizzardAuthService(WebClient blizzardAuthWebClient) {
        this.blizzardAuthWebClient = blizzardAuthWebClient;
    }


    @Scheduled(fixedDelay = 3600000) // A cada 1 hora
    public void refreshTokenScheduled() {
        logger.debug("reparando token...");
        this.cachedToken = null;
        fetchAccessToken();
    }

    public synchronized String fetchAccessToken() {
        if (cachedToken != null && !cachedToken.isExpired()) {
            logger.debug("token em cache ainda é válido, retornando ele...");
            return cachedToken.getAccessToken();
        }

        // PRECISA SER EM BASE64: clientId:clientSecret - SE MUDAR FUTURAMENTE LEMBRAR!!!!
        BlizzardTokenDto response = blizzardAuthWebClient
                .post()
                .uri("/token")
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(BlizzardTokenDto.class)
                .block();

        if (response == null) throw new RuntimeException();

        this.cachedToken = response;
        return response.getAccessToken();
    }

    public BlizzardTokenDto getTokenStatus() {
        return cachedToken;
    }

}
