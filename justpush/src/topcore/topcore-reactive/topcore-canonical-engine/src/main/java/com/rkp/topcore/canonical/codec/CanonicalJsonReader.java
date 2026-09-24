package com.rkp.topcore.canonical.codec;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Converts downstream JSON into a generic
 * Map<String, Object> representation.
 *
 * This class is responsible only for JSON parsing.
 *
 * It does not know:
 *
 * - TSAS
 * - T
 * - P
 * - Canonical fields
 * - Runtime mappings
 * - Business rules
 *
 * CanonicalEngine is responsible for converting
 * the resulting Map into a CanonicalDocument.
 */
@Component
public class CanonicalJsonReader {

    private final ObjectMapper objectMapper;


    public CanonicalJsonReader(
            ObjectMapper objectMapper) {

        if (objectMapper == null) {

            throw new IllegalArgumentException(
                    "ObjectMapper cannot be null"
            );
        }

        this.objectMapper =
                objectMapper;
    }


    /**
     * Parse JSON into a generic map.
     *
     * Example:
     *
     * {
     *     "responseCode": "00",
     *     "responseMessage": "APPROVED"
     * }
     *
     * becomes:
     *
     * Map<String,Object>
     *
     * The map is subsequently passed to
     * CanonicalEngine.createInbound().
     */
    public Map<String, Object> read(
            String json) {

        if (json == null ||
                json.isBlank()) {

            throw new IllegalArgumentException(
                    "JSON cannot be null or blank"
            );
        }

        try {

            return objectMapper.readValue(
                    json,
                    new TypeReference<
                            Map<String, Object>>() {}
            );

        } catch (JsonProcessingException e) {

            throw new IllegalArgumentException(
                    "Unable to parse downstream JSON",
                    e
            );
        }
    }
}