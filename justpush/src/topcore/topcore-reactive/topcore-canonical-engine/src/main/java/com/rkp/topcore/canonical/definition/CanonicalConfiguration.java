package com.rkp.topcore.canonical.definition;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CanonicalConfiguration {

    private ConfigurationSettings settings =
            new ConfigurationSettings();


    /*
     * =========================================================
     * INBOUND CANONICAL FIELDS
     * =========================================================
     *
     * Fields used by the request-side CanonicalDocument.
     *
     * Example:
     *
     * B-200 request
     *      ↓
     * B-200-Inbound.pan
     *      ↓
     * Inbound CanonicalDocument
     *      ↓
     * TC-Fields-Inbound.pan
     */
    private Map<String, CanonicalFieldDefinition>
            inboundFields =
            new LinkedHashMap<>();


    /*
     * =========================================================
     * OUTBOUND CANONICAL FIELDS
     * =========================================================
     *
     * Fields used by the response-side CanonicalDocument.
     *
     * Example:
     *
     * TSAS response
     *      ↓
     * TSAS-Inbound.pan
     *      ↓
     * Response CanonicalDocument
     *      ↓
     * TC-Fields-Outbound.pan
     */
    private Map<String, CanonicalFieldDefinition>
            outboundFields =
            new LinkedHashMap<>();


    /*
     * =========================================================
     * SETTINGS
     * =========================================================
     */

    public ConfigurationSettings getSettings() {

        return settings;
    }


    public void setSettings(
            ConfigurationSettings settings) {

        if (settings == null) {

            this.settings =
                    new ConfigurationSettings();

        } else {

            this.settings =
                    settings;
        }
    }


    /*
     * =========================================================
     * INBOUND FIELDS
     * =========================================================
     */

    public Map<String, CanonicalFieldDefinition>
            getInboundFields() {

        return Collections.unmodifiableMap(
                inboundFields
        );
    }


    public void setInboundFields(
            Map<String, CanonicalFieldDefinition>
                    inboundFields) {

        this.inboundFields =
                new LinkedHashMap<>();

        if (inboundFields != null) {

            inboundFields.forEach(
                    (key, value) ->
                            this.inboundFields.put(
                                    normalizeFieldId(key),
                                    value
                            )
            );
        }
    }


    public CanonicalFieldDefinition
            getInboundFieldDefinition(
                    String fieldId) {

        return inboundFields.get(
                normalizeFieldId(fieldId)
        );
    }


    /*
     * =========================================================
     * OUTBOUND FIELDS
     * =========================================================
     */

    public Map<String, CanonicalFieldDefinition>
            getOutboundFields() {

        return Collections.unmodifiableMap(
                outboundFields
        );
    }


    public void setOutboundFields(
            Map<String, CanonicalFieldDefinition>
                    outboundFields) {

        this.outboundFields =
                new LinkedHashMap<>();

        if (outboundFields != null) {

            outboundFields.forEach(
                    (key, value) ->
                            this.outboundFields.put(
                                    normalizeFieldId(key),
                                    value
                            )
            );
        }
    }


    public CanonicalFieldDefinition
            getOutboundFieldDefinition(
                    String fieldId) {

        return outboundFields.get(
                normalizeFieldId(fieldId)
        );
    }


    /*
     * =========================================================
     * FIELD NORMALIZATION
     * =========================================================
     */

    private String normalizeFieldId(
            String fieldId) {

        if (fieldId == null ||
                fieldId.isBlank()) {

            throw new IllegalArgumentException(
                    "Canonical field ID cannot be null or blank"
            );
        }

        String id =
                fieldId.trim();

        return id.startsWith("TF")
                ? id
                : "TF" + id;
    }


    /*
     * =========================================================
     * GLOBAL TOPCORE SETTINGS
     * =========================================================
     */

    public static class ConfigurationSettings {

        private boolean isValidationEnabled = true;


        @JsonProperty("isValidationEnabled")
        public boolean isValidationEnabled() {

            return isValidationEnabled;
        }


        @JsonProperty("isValidationEnabled")
        public void setValidationEnabled(
                boolean validationEnabled) {

            isValidationEnabled =
                    validationEnabled;
        }
    }
}