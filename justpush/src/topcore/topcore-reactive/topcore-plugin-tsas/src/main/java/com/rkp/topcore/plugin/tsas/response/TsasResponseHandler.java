package com.rkp.topcore.plugin.tsas.response;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.engine.CanonicalEngine;
import com.rkp.topcore.core.context.TopCoreContext;

import reactor.core.publisher.Mono;

@Component
public class TsasResponseHandler {

    private final CanonicalEngine canonicalEngine;
    private final ObjectMapper objectMapper;

    public TsasResponseHandler(
            CanonicalEngine canonicalEngine,
            ObjectMapper objectMapper) {

        if (canonicalEngine == null) {
            throw new IllegalArgumentException(
                    "CanonicalEngine cannot be null");
        }

        if (objectMapper == null) {
            throw new IllegalArgumentException(
                    "ObjectMapper cannot be null");
        }

        this.canonicalEngine =
                canonicalEngine;

        this.objectMapper =
                objectMapper;
    }

    public Mono<Void> handle(
            TopCoreContext context,
            Map<String, Object> response) {

        if (context == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "TopCoreContext cannot be null"));
        }

        if (response == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "TSAS response cannot be null"));
        }

        final String responseJson;

        try {
            responseJson =
                    objectMapper.writeValueAsString(
                            response);
        } catch (Exception ex) {
            return Mono.error(ex);
        }

        try {
            CanonicalDocument responseDocument =
                    canonicalEngine.createResponse(
                            "TSAS",
                            response);

            context.setDownstreamResponseCanonicalDocument(
                    "TSAS",
                    responseDocument);

            context.downstream("TSAS")
                    .setResponsePayload(
                            responseJson);

            context.downstream("TSAS")
                    .markEnd();

            return Mono.empty();

        } catch (Exception ex) {
            return Mono.error(ex);
        }
    }
}