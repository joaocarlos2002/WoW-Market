package com.wowmarket.wowmarket.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "blizzard")
@Validated
public class WebClientConfig {
    private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    @NotBlank(message = "['blizzard.api-base-url'] deve ser configurado e não pode ser vazio")
    private String apiBaseUrl;
    @NotBlank(message = "['blizzard.auth-base-url'] - deve ser configurado e não pode ser vazio")
    private String authBaseUrl;

    @Bean
    public WebClient blizzardApiWebClient() {
        HttpClient httpClient = HttpClient.create()
                .compress(true);

        return WebClient.builder()
                .baseUrl(apiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(errorHandler())
                .build();
    }

    @Bean
    public WebClient blizzardAuthWebClient() {
        HttpClient httpClient = HttpClient.create()
                .compress(true);

        return WebClient.builder()
                .baseUrl(authBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .filter(errorHandler())
                .build();
    }

    private ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {

            if (response.statusCode().isError()) {

                return response.bodyToMono(String.class)
                        .flatMap(body -> {

                            if (response.statusCode().value() == 401)
                                logger.error("[BLIZZARD API ERROR] - Status 401 - Token de acesso inválido ou expirado");

                            if (response.statusCode().value() == 429)
                                logger.error("[BLIZZARD API ERROR] - Status 429 - Muitas requisições");

                            if (response.statusCode().is5xxServerError())
                                logger.error("[BLIZZARD API ERROR] - Status: {}, Body: {}", response.statusCode(), body);

                            return Mono.error(new RuntimeException(body));
                        });
            }

            return Mono.just(response);
        });

    }
}
