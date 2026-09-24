package com.rkp.topcore.plugin.tsas;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.engine.CanonicalEngine;
import com.rkp.topcore.core.context.TopCoreContext;
import com.rkp.topcore.core.downstream.DownstreamClient;
import com.rkp.topcore.plugin.tsas.auth.TsasTokenService;
import com.rkp.topcore.plugin.tsas.http.TsasHttpClient;
import com.rkp.topcore.plugin.tsas.response.TsasResponseHandler;

import reactor.core.publisher.Mono;

@Component
public class TsasClient implements DownstreamClient {

    private static final String INTEGRATION_ID = "TSAS";

    private final CanonicalEngine canonicalEngine;
    private final ObjectMapper objectMapper;
    private final TsasTokenService tokenService;
    private final TsasHttpClient httpClient;
    private final TsasResponseHandler responseHandler;

    public TsasClient(
            CanonicalEngine canonicalEngine,
            ObjectMapper objectMapper,
            TsasTokenService tokenService,
            TsasHttpClient httpClient,
            TsasResponseHandler responseHandler) {

        if (canonicalEngine == null) {
            throw new IllegalArgumentException(
                    "CanonicalEngine cannot be null");
        }

        if (objectMapper == null) {
            throw new IllegalArgumentException(
                    "ObjectMapper cannot be null");
        }

        if (tokenService == null) {
            throw new IllegalArgumentException(
                    "TsasTokenService cannot be null");
        }

        if (httpClient == null) {
            throw new IllegalArgumentException(
                    "TsasHttpClient cannot be null");
        }

        if (responseHandler == null) {
            throw new IllegalArgumentException(
                    "TsasResponseHandler cannot be null");
        }

        this.canonicalEngine =
                canonicalEngine;

        this.objectMapper =
                objectMapper;

        this.tokenService =
                tokenService;

        this.httpClient =
                httpClient;

        this.responseHandler =
                responseHandler;
    }

    @Override
    public String integrationId() {
        return INTEGRATION_ID;
    }

    @Override
    public Mono<Void> call(
            TopCoreContext context) {

        if (context == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "TopCoreContext cannot be null"));
        }

        return Mono.defer(() -> {

            CanonicalDocument document =
                    context.getDownstreamRequestCanonicalDocument(
                            INTEGRATION_ID);

            if (document == null) {
                return Mono.error(
                        new IllegalStateException(
                                "TSAS request CanonicalDocument "
                                        + "is not available"));
            }

            final String requestJson;

            try {
                Map<String, Object> request =
                        canonicalEngine.createOutbound(
                                document,
                                INTEGRATION_ID);

                requestJson =
                        objectMapper.writeValueAsString(
                                request);

            } catch (Exception ex) {
                return Mono.error(ex);
            }

            context.downstream(
                    INTEGRATION_ID)
                    .markStart();

            return tokenService
                    .getValidToken()
                    .flatMap(accessToken ->
                            httpClient.post(
                                    accessToken,
                                    requestJson))
                    .flatMap(response ->
                            responseHandler.handle(
                                    context,
                                    response))
                    .then();

        });
    }
}