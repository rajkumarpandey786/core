package com.rkp.topcore.plugin.tsas.auth;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.rkp.topcore.core.config.GatewayConfig;
import com.rkp.topcore.core.downstream.WebClientFactory;

import reactor.core.publisher.Mono;

@Component
public class TsasTokenService {

    private final WebClient webClient;
    private final GatewayConfig config;

    private volatile TsasToken cachedToken;

    public TsasTokenService(
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

        this.webClient = webClientFactory.create("TSAS");
        this.config = config;
    }

    public Mono<String> getValidToken() {

        return Mono.defer(() -> {

            TsasToken token = cachedToken;

            if (token != null && token.isValid()) {
                return Mono.just(token.getAccessToken());
            }

            return requestNewToken()
                    .map(newToken -> {
                        cachedToken = newToken;
                        return newToken.getAccessToken();
                    });
        });
    }

    private Mono<TsasToken> requestNewToken() {

        GatewayConfig.Downstream.SystemConfig systemConfig =
                config.getDownstream()
                        .getSystems()
                        .get("TSAS");

        if (systemConfig == null) {
            return Mono.error(
                    new IllegalStateException(
                            "TSAS downstream configuration not found"));
        }

        GatewayConfig.Downstream.SystemConfig.Authentication authentication =
                systemConfig.getAuthentication();

        validateAuthentication(authentication);

        String formBody =
                "scope="
                        + URLEncoder.encode(
                                authentication.getScope(),
                                StandardCharsets.UTF_8)
                        + "&grant_type=client_credentials";

        return webClient
                .post()
                .uri(authentication.getTokenUrl())
                .headers(headers ->
                        headers.setBasicAuth(
                                authentication.getClientId(),
                                authentication.getClientSecret()))
                .contentType(
                        MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formBody)
                .retrieve()
                .bodyToMono(TsasTokenResponse.class)
                .flatMap(response ->
                        createToken(response, authentication));
    }

    private Mono<TsasToken> createToken(
            TsasTokenResponse response,
            GatewayConfig.Downstream.SystemConfig.Authentication authentication) {

        if (response == null) {
            return Mono.error(
                    new IllegalStateException(
                            "TSAS token response cannot be null"));
        }

        if (response.getAccessToken() == null ||
                response.getAccessToken().isBlank()) {

            return Mono.error(
                    new IllegalStateException(
                            "TSAS access token is missing"));
        }

        if (response.getExpiresIn() <= 0) {
            return Mono.error(
                    new IllegalStateException(
                            "TSAS token expires_in must be greater than zero"));
        }

        long refreshSkewMillis =
                authentication.getRefreshSkewSeconds() * 1000L;

        long expiresAt =
                System.currentTimeMillis()
                        + (response.getExpiresIn() * 1000L)
                        - refreshSkewMillis;

        return Mono.just(
                new TsasToken(
                        response.getAccessToken(),
                        expiresAt));
    }

    private void validateAuthentication(
            GatewayConfig.Downstream.SystemConfig.Authentication authentication) {

        if (authentication == null) {
            throw new IllegalStateException(
                    "TSAS authentication configuration cannot be null");
        }

        if (authentication.getTokenUrl() == null ||
                authentication.getTokenUrl().isBlank()) {

            throw new IllegalStateException(
                    "TSAS tokenUrl cannot be null or blank");
        }

        if (authentication.getClientId() == null ||
                authentication.getClientId().isBlank()) {

            throw new IllegalStateException(
                    "TSAS clientId cannot be null or blank");
        }

        if (authentication.getClientSecret() == null ||
                authentication.getClientSecret().isBlank()) {

            throw new IllegalStateException(
                    "TSAS clientSecret cannot be null or blank");
        }

        if (authentication.getScope() == null ||
                authentication.getScope().isBlank()) {

            throw new IllegalStateException(
                    "TSAS scope cannot be null or blank");
        }

        if (authentication.getRefreshSkewSeconds() < 0) {
            throw new IllegalStateException(
                    "TSAS refreshSkewSeconds cannot be negative");
        }
    }
}