package com.rkp.topcore.canonical.engine;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.mapping.MappingDirection;
import com.rkp.topcore.canonical.runtime.RuntimeMapping;

@Component
public class InboundMappingEngine {

    public CanonicalDocument map(
            Map<String, Object> sourceData,
            RuntimeMapping runtimeMapping,
            CanonicalDocument document) {

        if (sourceData == null) {

            throw new IllegalArgumentException(
                    "Source data cannot be null"
            );
        }

        if (runtimeMapping == null) {

            throw new IllegalArgumentException(
                    "Runtime mapping cannot be null"
            );
        }

        if (document == null) {

            throw new IllegalArgumentException(
                    "Canonical document cannot be null"
            );
        }

        if (runtimeMapping.getDirection()
                != MappingDirection.INBOUND) {

            throw new IllegalArgumentException(
                    "Runtime mapping must be INBOUND"
            );
        }


        /*
         * Inbound mapping:
         *
         * source field -> canonical TF field
         *
         * Example:
         *
         * base.record.Country
         *              |
         *              v
         *            TF001
         *
         * For INBOUND mappings, each source is
         * expected to have one canonical target.
         */
        for (Map.Entry<String, List<String>> mapping :
                runtimeMapping
                        .getSourceToTargets()
                        .entrySet()) {

            String source =
                    mapping.getKey();

            List<String> targets =
                    mapping.getValue();


            /*
             * A source field must have exactly
             * one canonical target for inbound
             * processing.
             */
            if (targets == null
                    || targets.size() != 1) {

                throw new IllegalArgumentException(
                        "Inbound source must have exactly "
                                + "one canonical target: "
                                + source
                );
            }


            Object value =
                    sourceData.get(source);


            /*
             * Do not add null source values
             * to the canonical document.
             */
            if (value == null) {

                continue;
            }


            String canonicalField =
                    targets.get(0);


            document.addTF(
                    canonicalField,
                    value
            );
        }


        return document;
    }
}