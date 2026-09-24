package com.rkp.topcore.canonical.document;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkp.topcore.canonical.definition.CanonicalConfiguration;

@Component
public class CanonicalDocumentFactory {

    private final CanonicalConfiguration configuration;

    private final ObjectMapper objectMapper;


    public CanonicalDocumentFactory(
            CanonicalConfiguration configuration,
            ObjectMapper objectMapper) {

        if (configuration == null) {

            throw new IllegalArgumentException(
                    "Canonical configuration cannot be null"
            );
        }

        if (objectMapper == null) {

            throw new IllegalArgumentException(
                    "ObjectMapper cannot be null"
            );
        }

        this.configuration =
                configuration;

        this.objectMapper =
                objectMapper;
    }


    /*
     * =========================================================
     * REQUEST DOCUMENT
     * =========================================================
     *
     * Uses:
     *
     * TC-Fields-Inbound.pan
     *
     * This document represents the canonical request
     * entering TopCore.
     */
    public CanonicalDocument createRequest() {

        return new CanonicalDocument(
                configuration,
                objectMapper,
                CanonicalDocumentType.REQUEST
        );
    }


    /*
     * =========================================================
     * RESPONSE DOCUMENT
     * =========================================================
     *
     * Uses:
     *
     * TC-Fields-Outbound.pan
     *
     * This document represents the canonical response
     * being built for the upstream system.
     */
    public CanonicalDocument createResponse() {

        return new CanonicalDocument(
                configuration,
                objectMapper,
                CanonicalDocumentType.RESPONSE
        );
    }
}