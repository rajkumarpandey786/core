package com.rkp.topcore.canonical.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.engine.CanonicalEngine;
import com.rkp.topcore.canonical.engine.OutboundMappingEngine;
import com.rkp.topcore.canonical.runtime.RuntimeMapping;

import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Converts a CanonicalDocument into downstream JSON
 * using the configured OUTBOUND mapping for an integration.
 *
 * The mapping configuration is loaded and compiled
 * during application initialization.
 *
 * Responsibility of this class:
 *
 * CanonicalDocument
 *        ↓
 * OutboundMappingEngine
 *        ↓
 * Map<String, Object>
 *        ↓
 * ObjectMapper
 *        ↓
 * JSON
 *
 * This class contains no downstream-specific business
 * logic. It does not know about TSAS, PAIMI, or any
 * other particular downstream system.
 */
@Component
public class CanonicalJsonWriter {

    private final OutboundMappingEngine outboundMappingEngine;

    private final CanonicalEngine canonicalEngine;

    private final ObjectMapper objectMapper;


    public CanonicalJsonWriter(
            OutboundMappingEngine outboundMappingEngine,
            CanonicalEngine canonicalEngine,
            ObjectMapper objectMapper) {

        if (outboundMappingEngine == null) {

            throw new IllegalArgumentException(
                    "OutboundMappingEngine cannot be null"
            );
        }

        if (canonicalEngine == null) {

            throw new IllegalArgumentException(
                    "CanonicalEngine cannot be null"
            );
        }

        if (objectMapper == null) {

            throw new IllegalArgumentException(
                    "ObjectMapper cannot be null"
            );
        }

        this.outboundMappingEngine =
                outboundMappingEngine;

        this.canonicalEngine =
                canonicalEngine;

        this.objectMapper =
                objectMapper;
    }


    /**
     * Converts a CanonicalDocument into JSON using
     * the configured OUTBOUND mapping for the supplied
     * integration.
     *
     * Example:
     *
     * CanonicalDocument
     *        ↓
     * TSAS OUTBOUND mapping
     *        ↓
     * Map<String, Object>
     *        ↓
     * JSON
     *
     * The same method works for any configured
     * downstream integration.
     *
     * Example integrations:
     *
     * TSAS
     * PAIMI
     * or any future integration.
     *
     * Structured values such as:
     *
     * Map<String, Object>
     * List<Object>
     * List<Map<String, Object>>
     *
     * are serialized by Jackson without any
     * downstream-specific handling here.
     */
    public String write(
            CanonicalDocument document,
            String integrationId) {

        if (document == null) {

            throw new IllegalArgumentException(
                    "Canonical document cannot be null"
            );
        }

        if (integrationId == null ||
                integrationId.isBlank()) {

            throw new IllegalArgumentException(
                    "Integration ID cannot be null or blank"
            );
        }


        String normalizedIntegrationId =
                integrationId.trim();


        /*
         * Obtain the already compiled OUTBOUND mapping
         * from CanonicalEngine.
         *
         * Mapping lookup happens against configuration
         * that was loaded during application startup.
         */
        RuntimeMapping runtimeMapping =
                canonicalEngine.getOutboundMapping(
                        normalizedIntegrationId
                );


        /*
         * Convert the canonical document into the
         * downstream object structure.
         *
         * OutboundMappingEngine is responsible for
         * applying the runtime mapping.
         */
        Map<String, Object> output =
                outboundMappingEngine.map(
                        document,
                        runtimeMapping
                );


        /*
         * Serialize the resulting object structure.
         *
         * Jackson handles:
         *
         * String
         * Number
         * Boolean
         * Map
         * List
         * nested Map/List combinations
         *
         * automatically.
         */
        try {

            return objectMapper.writeValueAsString(
                    output
            );

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Unable to convert canonical document "
                            + "to downstream JSON for integration: "
                            + normalizedIntegrationId,
                    e
            );
        }
    }
}
