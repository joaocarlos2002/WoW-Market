package com.wowmarket.wowmarket.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;



@Configuration
@ConfigurationProperties(prefix = "blizzard")
public class WebClientConfig {
    private String apiBaseUrl;
    private String authBaseUrl;

    @Bean
    public WebClient blizzardApiWebClient() {
        return WebClient.builder()
                .baseUrl(apiBaseUrl)
                .filter(errorHandler())
                .build();
    }
    // Autenticacao do cliente (OAuth)
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
                                return Mono.error(new RuntimeException("Muitas requisiÇões"));

                            if (response.statusCode().is5xxServerError())
                                return Mono.error(new RuntimeException("Erro Interno na Blizzard"));

                            return Mono.error(new RuntimeException(body));
                        });
            }

            return Mono.just(response);
        });

    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getAuthBaseUrl() {
        return authBaseUrl;
    }

    public void setAuthBaseUrl(String authBaseUrl) {
        this.authBaseUrl = authBaseUrl;
    }
}
