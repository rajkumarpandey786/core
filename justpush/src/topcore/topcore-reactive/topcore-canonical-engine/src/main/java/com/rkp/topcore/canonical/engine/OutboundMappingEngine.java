package com.rkp.topcore.canonical.engine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.mapping.MappingDirection;
import com.rkp.topcore.canonical.path.JsonPathWriter;
import com.rkp.topcore.canonical.runtime.RuntimeMapping;

@Component
public class OutboundMappingEngine {

/*
 * =========================================================
 * LOGGER
 * =========================================================
 */
private static final Logger log =
        LoggerFactory.getLogger(
                OutboundMappingEngine.class
        );


/*
 * =========================================================
 * DEPENDENCIES
 * =========================================================
 */
private final JsonPathWriter jsonPathWriter;


public OutboundMappingEngine(
        JsonPathWriter jsonPathWriter) {

    if (jsonPathWriter == null) {

        throw new IllegalArgumentException(
                "JsonPathWriter cannot be null"
        );
    }

    this.jsonPathWriter =
            jsonPathWriter;
}


/**
 * Convert a canonical document into the
 * downstream object structure defined by
 * the outbound runtime mapping.
 *
 * One canonical field may map to multiple
 * downstream fields.
 *
 * Example:
 *
 * TF001
 *   -> country
 *   -> transaction.country
 *
 * Both targets receive the same canonical value.
 */
public Map<String, Object> map(
        CanonicalDocument document,
        RuntimeMapping runtimeMapping) {

    /*
     * =====================================================
     * VALIDATION
     * =====================================================
     */
    if (document == null) {

        throw new IllegalArgumentException(
                "Canonical document cannot be null"
        );
    }

    if (runtimeMapping == null) {

        throw new IllegalArgumentException(
                "Runtime mapping cannot be null"
        );
    }

    if (runtimeMapping.getDirection()
            != MappingDirection.OUTBOUND) {

        throw new IllegalArgumentException(
                "Runtime mapping must be OUTBOUND"
        );
    }


    /*
     * =====================================================
     * START LOGGING
     * =====================================================
     */
    log.info(
            "Outbound mapping started integration={} direction={}",
            runtimeMapping.getIntegrationId(),
            runtimeMapping.getDirection()
    );


    Map<String, Object> output =
            new LinkedHashMap<>();


    /*
     * =====================================================
     * PROCESS MAPPINGS
     * =====================================================
     */
    for (Map.Entry<
            String,
            List<String>> mapping :
            runtimeMapping
                    .getSourceToTargets()
                    .entrySet()) {

        String source =
                mapping.getKey();

        List<String> targets =
                mapping.getValue();


        /*
         * -------------------------------------------------
         * READ CANONICAL VALUE
         * -------------------------------------------------
         */
        Object value =
                document.getTF(source);


        /*
         * -------------------------------------------------
         * LOG SOURCE VALUE
         * -------------------------------------------------
         
        log.debug(
                "Outbound mapping evaluating integration={} source={} value={} targets={}",
                runtimeMapping.getIntegrationId(),
                source,
                value,
                targets
        );
         */

        /*
         * Missing canonical value does not produce
         * an output field.
         */
        if (value == null) {

           /* log.debug(
                    "Outbound mapping skipped integration={} source={} reason=NULL_VALUE",
                    runtimeMapping.getIntegrationId(),
                    source
            );*/

            continue;
        }


        /*
         * No target configured.
         */
        if (targets == null ||
                targets.isEmpty()) {

           /* log.warn(
                    "Outbound mapping skipped integration={} source={} reason=NO_TARGETS",
                    runtimeMapping.getIntegrationId(),
                    source
            );*/

            continue;
        }


        /*
         * =================================================
         * WRITE TARGETS
         * =================================================
         *
         * One canonical field may populate
         * multiple downstream fields.
         */
        for (String target : targets) {

            if (target == null ||
                    target.isBlank()) {

               /* log.warn(
                        "Outbound mapping skipped integration={} source={} reason=BLANK_TARGET",
                        runtimeMapping.getIntegrationId(),
                        source
                );*/

                continue;
            }


            /*
             * ---------------------------------------------
             * LOG ACTUAL MAPPING
             * ---------------------------------------------
             
            log.info(
                    "Outbound mapping integration={} source={} value={} target={}",
                    runtimeMapping.getIntegrationId(),
                    source,
                    value,
                    target
            );

             */
            /*
             * ---------------------------------------------
             * WRITE VALUE INTO OUTPUT STRUCTURE
             * ---------------------------------------------
             */
            jsonPathWriter.write(
                    output,
                    target,
                    value
            );


            /*
             * ---------------------------------------------
             * LOG CURRENT OUTPUT STRUCTURE
             * ---------------------------------------------
             
            log.debug(
                    "Outbound mapping current output integration={} output={}",
                    runtimeMapping.getIntegrationId(),
                    output
            );
            */
        }
    }


    /*
     * =====================================================
     * FINAL OUTPUT
     * =====================================================
     */
    log.info(
            "Outbound mapping completed integration={} output={}",
            runtimeMapping.getIntegrationId(),
            output
    );


    return output;
}

}
