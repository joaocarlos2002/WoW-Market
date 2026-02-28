package com.wowmarket.wowmarket.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "blizzard")
public class WebClientConfig {
    @NotBlank(message = "'blizzard.api-base-url' deve ser configurado e não pode ser vazio")
    private String apiBaseUrl;
    @NotBlank(message = "'blizzard.auth-base-url' deve ser configurado e não pode ser vazio")
    private String authBaseUrl;

    @Bean
    public WebClient blizzardApiWebClient() {
        return WebClient.builder()
                .baseUrl(apiBaseUrl)
                .filter(errorHandler())
                .build();
    }

    @Bean
    public WebClient blizzardAuthWebClient() {
        return WebClient.builder()
                .baseUrl(authBaseUrl)
                .filter(errorHandler())
                .build();
    }

    private ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {

            if (response.statusCode().isError()) {

                return response.bodyToMono(String.class)
                        .flatMap(body -> {

                            if (response.statusCode().value() == 401)
                                return Mono.error(new RuntimeException("token não autorizado"));

                            if (response.statusCode().value() == 429)
                                return Mono.error(new RuntimeException("Muitas requisições"));

                            if (response.statusCode().is5xxServerError())
                                return Mono.error(new RuntimeException("Erro Interno na Blizzard"));

                            return Mono.error(new RuntimeException(body));
                        });
            }

            return Mono.just(response);
        });

    }
}
