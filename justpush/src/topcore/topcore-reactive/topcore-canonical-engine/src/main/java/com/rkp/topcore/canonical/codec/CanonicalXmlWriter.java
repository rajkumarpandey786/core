package com.rkp.topcore.canonical.codec;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.engine.CanonicalEngine;
import com.rkp.topcore.canonical.engine.OutboundMappingEngine;
import com.rkp.topcore.canonical.runtime.RuntimeMapping;

/**
 * Converts a CanonicalDocument into an XML representation
 * using the configured OUTBOUND mapping for an integration.
 *
 * Example:
 *
 * Common Response CanonicalDocument
 *              |
 *              v
 *       B-200-Outbound.pan
 *              |
 *              v
 *             XML
 *
 * Mapping configuration is loaded and compiled during
 * application initialization.
 */
@Component
public class CanonicalXmlWriter {

    private final CanonicalEngine canonicalEngine;

    private final OutboundMappingEngine outboundMappingEngine;


    public CanonicalXmlWriter(
            CanonicalEngine canonicalEngine,
            OutboundMappingEngine outboundMappingEngine) {

        if (canonicalEngine == null) {

            throw new IllegalArgumentException(
                    "CanonicalEngine cannot be null"
            );
        }

        if (outboundMappingEngine == null) {

            throw new IllegalArgumentException(
                    "OutboundMappingEngine cannot be null"
            );
        }

        this.canonicalEngine =
                canonicalEngine;

        this.outboundMappingEngine =
                outboundMappingEngine;
    }


    /**
     * Convert a CanonicalDocument into XML using
     * the configured OUTBOUND mapping.
     *
     * Example:
     *
     * write(commonResponseDocument, "B-200")
     *
     * uses:
     *
     * B-200-Outbound.pan
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


        /*
         * Get the already compiled OUTBOUND mapping.
         */
        RuntimeMapping runtimeMapping =
                canonicalEngine.getOutboundMapping(
                        integrationId
                );


        /*
         * Convert the CanonicalDocument into the
         * mapped object structure.
         */
        Map<String, Object> output =
                outboundMappingEngine.map(
                        document,
                        runtimeMapping
                );


        /*
         * XML serialization will be implemented here.
         */
        return convertToXml(
                output,
                integrationId
        );
    }


    private String convertToXml(
            Map<String, Object> output,
            String integrationId) {

        if (output == null) {

            throw new IllegalArgumentException(
                    "XML output cannot be null"
            );
        }

        /*
         * XML serialization implementation will be
         * added after we finalize the XML mapping
         * structure in B-200-Outbound.pan.
         */

        throw new UnsupportedOperationException(
                "XML serialization is not implemented yet "
                        + "for integration: "
                        + integrationId
        );
    }
}