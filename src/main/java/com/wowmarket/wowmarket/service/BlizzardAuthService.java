package com.wowmarket.wowmarket.service;

import com.wowmarket.wowmarket.dto.BlizzardTokenDto;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
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

    @Value("${blizzard.client-id:}")
    private String clientId;

    @Value("${blizzard.client-secret:}")
    private String clientSecret;

    private final WebClient blizzardAuthWebClient;
    private BlizzardTokenDto cachedToken;

    public BlizzardAuthService(WebClient blizzardAuthWebClient) {
        this.blizzardAuthWebClient = blizzardAuthWebClient;
    }

    @PostConstruct
    public void initializeToken() {
        System.out.println("========== [BLIZZARD AUTH] Inicializando token da Blizzard... ==========");
        try {
            fetchAccessToken();
            System.out.println("========== [BLIZZARD AUTH] Token inicializado com sucesso! ==========");
        } catch (Exception e) {
            System.err.println("========== [BLIZZARD AUTH] ERRO ao inicializar token: " + e.getMessage() + " ==========");
        }
    }

    @Scheduled(fixedDelay = 60000) // Refresh a cada 10 minutos
    public void refreshTokenScheduled() {
        System.out.println("========== [BLIZZARD AUTH] Refresh agendado do token... ==========");
        this.cachedToken = null;
        fetchAccessToken();
    }

    public synchronized String fetchAccessToken() {
        // Se o token em cache ainda é válido, retorna ele
        if (cachedToken != null && !cachedToken.isExpired()) {
            System.out.println("✓ [BLIZZARD AUTH] Token em cache válido - Expira em: " + (cachedToken.getObtainedAtEpochSec() + cachedToken.getExpiresIn()) + " (unix timestamp)");
            return cachedToken.getAccessToken();
        }

        System.out.println("→ [BLIZZARD AUTH] Fazendo POST para oauth.battle.net/token com Basic Auth...");

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

        assert response != null;
        long expiresAt = response.getObtainedAtEpochSec() + response.getExpiresIn();
        System.out.println("✓ [BLIZZARD AUTH] Novo token obtido com sucesso!");
        System.out.println("  - Token: " + response.getAccessToken().substring(0, Math.min(20, response.getAccessToken().length())) + "...");
        System.out.println("  - Tipo: " + response.getTokenType());
        System.out.println("  - Expira em: " + expiresAt + " (unix timestamp)");
        System.out.println("  - Válido por: " + response.getExpiresIn() + " segundos");

        this.cachedToken = response;
        return response.getAccessToken();
    }

    public BlizzardTokenDto getTokenStatus() {
        return cachedToken;
    }

}
