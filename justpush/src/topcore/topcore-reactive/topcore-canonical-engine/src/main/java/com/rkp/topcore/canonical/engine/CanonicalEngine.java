package com.rkp.topcore.canonical.engine;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.document.CanonicalDocumentFactory;
import com.rkp.topcore.canonical.mapping.CanonicalMappingRegistry;
import com.rkp.topcore.canonical.mapping.MappingDirection;
import com.rkp.topcore.canonical.runtime.RuntimeMapping;


@Component
public final class CanonicalEngine {

    private final CanonicalMappingRegistry registry;

    private final InboundMappingEngine inboundMappingEngine;

    private final OutboundMappingEngine outboundMappingEngine;

    private final CanonicalDocumentFactory documentFactory;


    public CanonicalEngine(
            CanonicalMappingRegistry registry,
            InboundMappingEngine inboundMappingEngine,
            OutboundMappingEngine outboundMappingEngine,
            CanonicalDocumentFactory documentFactory) {

        if (registry == null) {

            throw new IllegalArgumentException(
                    "Canonical mapping registry cannot be null"
            );
        }

        if (inboundMappingEngine == null) {

            throw new IllegalArgumentException(
                    "Inbound mapping engine cannot be null"
            );
        }

        if (outboundMappingEngine == null) {

            throw new IllegalArgumentException(
                    "Outbound mapping engine cannot be null"
            );
        }

        if (documentFactory == null) {

            throw new IllegalArgumentException(
                    "Canonical document factory cannot be null"
            );
        }

        this.registry =
                registry;

        this.inboundMappingEngine =
                inboundMappingEngine;

        this.outboundMappingEngine =
                outboundMappingEngine;

        this.documentFactory =
                documentFactory;
    }


    /*
     * =========================================================
     * REQUEST
     * =========================================================
     */

    /**
     * Convert upstream source data into a
     * REQUEST CanonicalDocument.
     *
     * Example:
     *
     * B-200 XML
     *      ↓
     * B-200-Inbound.pan
     *      ↓
     * REQUEST CanonicalDocument
     */
    public CanonicalDocument createInbound(
            String integrationId,
            Map<String, Object> sourceData) {

        validateIntegrationId(
                integrationId
        );

        if (sourceData == null) {

            throw new IllegalArgumentException(
                    "Inbound source data cannot be null"
            );
        }

        RuntimeMapping runtimeMapping =
                registry.getInbound(
                        integrationId
                );

        if (runtimeMapping == null) {

            throw new IllegalArgumentException(
                    "No INBOUND mapping configured for integration: "
                            + integrationId
            );
        }

        CanonicalDocument document =
                documentFactory.createRequest();

        inboundMappingEngine.map(
                sourceData,
                runtimeMapping,
                document
        );

        return document;
    }


    /*
     * =========================================================
     * RESPONSE
     * =========================================================
     */

    /**
     * Convert downstream response data into a
     * RESPONSE CanonicalDocument.
     *
     * Example:
     *
     * TSAS JSON response
     *        ↓
     * TSAS-Inbound.pan
     *        ↓
     * RESPONSE CanonicalDocument
     *
     *
     * The integration mapping is INBOUND because
     * the response is entering TopCore from the
     * downstream integration.
     *
     * The CanonicalDocument is RESPONSE because
     * it represents the response side of the
     * TopCore transaction.
     */
    public CanonicalDocument createResponse(
            String integrationId,
            Map<String, Object> sourceData) {

        validateIntegrationId(
                integrationId
        );

        if (sourceData == null) {

            throw new IllegalArgumentException(
                    "Response source data cannot be null"
            );
        }

        RuntimeMapping runtimeMapping =
                registry.getInbound(
                        integrationId
                );

        if (runtimeMapping == null) {

            throw new IllegalArgumentException(
                    "No INBOUND mapping configured for integration: "
                            + integrationId
            );
        }

        CanonicalDocument document =
                documentFactory.createResponse();

        inboundMappingEngine.map(
                sourceData,
                runtimeMapping,
                document
        );

        return document;
    }


    /*
     * =========================================================
     * OUTBOUND
     * =========================================================
     */

    /**
     * Convert a CanonicalDocument into downstream
     * data using the integration OUTBOUND mapping.
     */
    public Map<String, Object> createOutbound(
            CanonicalDocument document,
            String integrationId) {

        if (document == null) {

            throw new IllegalArgumentException(
                    "Canonical document cannot be null"
            );
        }

        validateIntegrationId(
                integrationId
        );

        RuntimeMapping runtimeMapping =
                getOutboundMapping(
                        integrationId
                );

        return outboundMappingEngine.map(
                document,
                runtimeMapping
        );
    }


    /*
     * =========================================================
     * OUTBOUND MAPPING ACCESS
     * =========================================================
     *
     * Used by CanonicalJsonWriter clients.
     *
     * The client does not access CanonicalMappingRegistry
     * directly.
     */

    /**
     * Return the compiled OUTBOUND runtime mapping
     * for an integration.
     *
     * Mapping configuration has already been loaded
     * and compiled during application startup.
     */
    public RuntimeMapping getOutboundMapping(
            String integrationId) {

        validateIntegrationId(
                integrationId
        );

        RuntimeMapping runtimeMapping =
                registry.getOutbound(
                        integrationId
                );

        if (runtimeMapping == null) {

            throw new IllegalArgumentException(
                    "No OUTBOUND mapping configured for integration: "
                            + integrationId
            );
        }

        return runtimeMapping;
    }


    /*
     * =========================================================
     * MAPPING CHECKS
     * =========================================================
     */

    public boolean hasInboundMapping(
            String integrationId) {

        validateIntegrationId(
                integrationId
        );

        return registry.contains(
                integrationId,
                MappingDirection.INBOUND
        );
    }


    public boolean hasOutboundMapping(
            String integrationId) {

        validateIntegrationId(
                integrationId
        );

        return registry.contains(
                integrationId,
                MappingDirection.OUTBOUND
        );
    }


    /*
     * =========================================================
     * REGISTRY
     * =========================================================
     */

    public int mappingCount() {

        return registry.size();
    }


    /*
     * =========================================================
     * VALIDATION
     * =========================================================
     */

    private void validateIntegrationId(
            String integrationId) {

        if (integrationId == null ||
                integrationId.isBlank()) {

            throw new IllegalArgumentException(
                    "Integration ID cannot be null or blank"
            );
        }
    }
}