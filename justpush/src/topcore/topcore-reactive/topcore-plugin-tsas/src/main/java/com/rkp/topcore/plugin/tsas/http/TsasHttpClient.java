package com.rkp.topcore.plugin.tsas.http;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.rkp.topcore.core.config.GatewayConfig;
import com.rkp.topcore.core.downstream.WebClientFactory;

import reactor.core.publisher.Mono;

@Component
public class TsasHttpClient {

    private final WebClient webClient;
    private final GatewayConfig config;

    public TsasHttpClient(
            WebClientFactory webClientFactory,
            GatewayConfig config) {

        if (webClientFactory == null) {
            throw new IllegalArgumentException(
                    "WebClientFactory cannot be null");
        }

        if (config == null) {
            throw new IllegalArgumentException(
                    "GatewayConfig cannot be null");
        }

        this.webClient =
                webClientFactory.create("TSAS");

        this.config =
                config;
    }

    public Mono<Map<String, Object>> post(
            String accessToken,
            String requestJson) {

        if (accessToken == null ||
                accessToken.isBlank()) {

            return Mono.error(
                    new IllegalArgumentException(
                            "TSAS access token cannot be null or blank"));
        }

        if (requestJson == null ||
                requestJson.isBlank()) {

            return Mono.error(
                    new IllegalArgumentException(
                            "TSAS request JSON cannot be null or blank"));
        }

        GatewayConfig.Downstream.SystemConfig
                systemConfig =
                config.getDownstream()
                        .getSystems()
                        .get("TSAS");

        if (systemConfig == null) {
            return Mono.error(
                    new IllegalStateException(
                            "No configuration found for "
                                    + "downstream TSAS"));
        }

        String url =
                systemConfig.getUrl();

        if (url == null || url.isBlank()) {
            return Mono.error(
                    new IllegalStateException(
                            "TSAS URL is not configured"));
        }

        return webClient
                .post()
                .uri(url)
                .headers(headers -> {
                    headers.setBearerAuth(accessToken);
                    headers.set(
                            "jms-tracking-id",
                            "12312");
                    headers.set(
                            "use-cache",
                            "false");
                    headers.set(
                            "x-payload-format",
                            "CARDS");
                })
                .contentType(
                        MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .retrieve()
                .bodyToMono(
                        new ParameterizedTypeReference<
                                Map<String, Object>>() {
                        });
    }
}