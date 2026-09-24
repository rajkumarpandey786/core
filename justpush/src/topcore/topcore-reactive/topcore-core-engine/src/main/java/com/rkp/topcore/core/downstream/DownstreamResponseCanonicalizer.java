package com.rkp.topcore.core.downstream;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.engine.CanonicalEngine;
import com.rkp.topcore.core.context.TopCoreContext;

@Component
public class DownstreamResponseCanonicalizer {

    private final CanonicalEngine canonicalEngine;


    public DownstreamResponseCanonicalizer(
            CanonicalEngine canonicalEngine) {

        if (canonicalEngine == null) {

            throw new IllegalArgumentException(
                    "CanonicalEngine cannot be null"
            );
        }

        this.canonicalEngine =
                canonicalEngine;
    }


    public CanonicalDocument canonicalize(
            TopCoreContext context,
            String systemName,
            Map<String, Object> responseData) {

        if (context == null) {

            throw new IllegalArgumentException(
                    "TopCoreContext cannot be null"
            );
        }

        if (systemName == null ||
                systemName.isBlank()) {

            throw new IllegalArgumentException(
                    "Downstream system name "
                            + "cannot be null or blank"
            );
        }

        if (responseData == null) {

            throw new IllegalArgumentException(
                    "Downstream response data "
                            + "cannot be null"
            );
        }


        /*
         * Downstream response
         *        ↓
         * Integration INBOUND mapping
         *        ↓
         * RESPONSE CanonicalDocument
         */
        CanonicalDocument responseDocument =
                canonicalEngine.createResponse(
                        systemName,
                        responseData
                );


        /*
         * Store the isolated response document
         * in TopCoreContext.
         */
        context.setDownstreamResponseCanonicalDocument(
                systemName,
                responseDocument
        );


        return responseDocument;
    }
}