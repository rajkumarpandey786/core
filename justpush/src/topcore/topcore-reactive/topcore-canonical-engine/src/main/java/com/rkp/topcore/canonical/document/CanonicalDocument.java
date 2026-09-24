package com.rkp.topcore.canonical.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkp.topcore.canonical.definition.CanonicalConfiguration;
import com.rkp.topcore.canonical.definition.CanonicalFieldDefinition;
import com.rkp.topcore.canonical.definition.DataType;
import com.rkp.topcore.canonical.definition.MaskingConfiguration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class CanonicalDocument {

    private final Map<String, CanonicalValue> fields =
            new LinkedHashMap<>();

    private final CanonicalConfiguration configuration;

    private final ObjectMapper objectMapper;

    private final CanonicalDocumentType documentType;


    public CanonicalDocument(
            CanonicalConfiguration configuration,
            ObjectMapper objectMapper,
            CanonicalDocumentType documentType) {

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

        if (documentType == null) {

            throw new IllegalArgumentException(
                    "Canonical document type cannot be null"
            );
        }

        this.configuration =
                configuration;

        this.objectMapper =
                objectMapper;

        this.documentType =
                documentType;
    }


    /*
     * =========================================================
     * DOCUMENT TYPE
     * =========================================================
     */

    public CanonicalDocumentType getDocumentType() {

        return documentType;
    }


    /*
     * =========================================================
     * ADD
     * =========================================================
     */

    public void addTF(
            String fieldId,
            Object value) {

        String key =
                normalizeFieldId(fieldId);

        validateFieldDefinition(key);

        if (fields.containsKey(key)) {

            throw new IllegalStateException(
                    "Canonical field already exists: "
                            + key
            );
        }

        validateFieldValue(
                key,
                value
        );

        fields.put(
                key,
                new CanonicalValue(value)
        );
    }


    /*
     * =========================================================
     * OVERRIDE
     * =========================================================
     */

    public boolean overrideTF(
            String fieldId,
            Object value) {

        String key =
                normalizeFieldId(fieldId);

        if (!fields.containsKey(key)) {

            return false;
        }

        validateFieldDefinition(key);

        validateFieldValue(
                key,
                value
        );

        fields.put(
                key,
                new CanonicalValue(value)
        );

        return true;
    }


    /*
     * =========================================================
     * REMOVE
     * =========================================================
     */

    public boolean removeTF(
            String fieldId) {

        String key =
                normalizeFieldId(fieldId);

        return fields.remove(key) != null;
    }


    /*
     * =========================================================
     * GET
     * =========================================================
     */

    public Object getTF(
            String fieldId) {

        String key =
                normalizeFieldId(fieldId);

        CanonicalValue value =
                fields.get(key);

        if (value == null) {

            return null;
        }

        return value.getValue();
    }

    /*
     * =========================================================
     * Override or Add TF
     * =========================================================
     */
    public void overrideOrAddTF(
            String fieldId,
            Object value) {

        String key =
                normalizeFieldId(fieldId);

        validateFieldDefinition(key);
        validateFieldValue(
                key,
                value
        );

        fields.put(
                key,
                new CanonicalValue(value)
        );
    }
    /*
     * =========================================================
     * KEYS
     * =========================================================
     */

    public List<String> getAllTFKeysASList() {

        List<String> keys =
                new ArrayList<>();

        for (String key : fields.keySet()) {

            keys.add(
                    removeTFPrefix(key)
            );
        }

        return keys;
    }


    /*
     * =========================================================
     * VALUES
     * =========================================================
     */

    public String getAllValues(
            String delimiter) {

        if (delimiter == null) {

            throw new IllegalArgumentException(
                    "Delimiter cannot be null"
            );
        }

        StringJoiner joiner =
                new StringJoiner(delimiter);

        for (CanonicalValue value :
                fields.values()) {

            if (value != null &&
                    value.getValue() != null) {

                joiner.add(
                        String.valueOf(
                                value.getValue()
                        )
                );
            }
        }

        return joiner.toString();
    }


    /*
     * =========================================================
     * JSON
     * =========================================================
     */

    public String getAsJson() {

        Map<String, Object> output =
                new LinkedHashMap<>();

        for (Map.Entry<String, CanonicalValue>
                entry : fields.entrySet()) {

            String fieldId =
                    entry.getKey();

            Object value =
                    entry.getValue()
                            .getValue();

            CanonicalFieldDefinition definition =
                    getFieldDefinition(
                            fieldId
                    );

            if (definition != null &&
                    definition.isMaskingRequired()) {

                value = mask(
                        value,
                        definition.getMasking()
                );
            }

            output.put(
                    fieldId,
                    value
            );
        }

        try {

            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(output);

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Unable to convert canonical document to JSON",
                    e
            );
        }
    }


    /*
     * =========================================================
     * SIZE
     * =========================================================
     */

    public int size() {

        return fields.size();
    }


    /*
     * =========================================================
     * COPY
     * =========================================================
     *
     * Creates an independent document containing the same
     * field values and the same REQUEST/RESPONSE type.
     */

    public CanonicalDocument copy() {

        CanonicalDocument copy =
                new CanonicalDocument(
                        this.configuration,
                        this.objectMapper,
                        this.documentType
                );

        for (Map.Entry<String, CanonicalValue>
                entry : this.fields.entrySet()) {

            copy.fields.put(
                    entry.getKey(),
                    new CanonicalValue(
                            entry.getValue()
                                    .getValue()
                    )
            );
        }

        return copy;
    }


    /*
     * =========================================================
     * FIELD DEFINITION
     * =========================================================
     *
     * Select the correct TC-Fields configuration according
     * to the type of this CanonicalDocument.
     */

    private CanonicalFieldDefinition
            getFieldDefinition(
                    String fieldId) {

        if (documentType ==
                CanonicalDocumentType.REQUEST) {

            return configuration
                    .getInboundFieldDefinition(
                            fieldId
                    );
        }

        return configuration
                .getOutboundFieldDefinition(
                        fieldId
                );
    }


    /*
     * =========================================================
     * FIELD DEFINITION VALIDATION
     * =========================================================
     */

    private void validateFieldDefinition(
            String fieldId) {

        CanonicalFieldDefinition definition =
                getFieldDefinition(
                        fieldId
                );

        if (definition == null) {

            String configurationName =
                    documentType ==
                            CanonicalDocumentType.REQUEST
                            ? "TC-Fields-Inbound.pan"
                            : "TC-Fields-Outbound.pan";

            throw new IllegalArgumentException(
                    "Canonical field is not defined in "
                            + configurationName
                            + ": "
                            + fieldId
            );
        }
    }


    /*
     * =========================================================
     * FIELD VALUE VALIDATION
     * =========================================================
     */

    private void validateFieldValue(
            String fieldId,
            Object value) {

        /*
         * Global validation switch.
         */
        if (!configuration
                .getSettings()
                .isValidationEnabled()) {

            return;
        }

        CanonicalFieldDefinition definition =
                getFieldDefinition(
                        fieldId
                );

        if (definition == null) {

            String configurationName =
                    documentType ==
                            CanonicalDocumentType.REQUEST
                            ? "TC-Fields-Inbound.pan"
                            : "TC-Fields-Outbound.pan";

            throw new IllegalArgumentException(
                    "Canonical field is not defined in "
                            + configurationName
                            + ": "
                            + fieldId
            );
        }


        /*
         * Individual field validation switch.
         */
        if (!definition.isFieldValidationEnabled()) {

            return;
        }


        /*
         * Required validation.
         */
        if (definition.isRequired()) {

            if (value == null ||
                    String.valueOf(value).isBlank()) {

                throw new IllegalArgumentException(
                        "Value is required for canonical field: "
                                + fieldId
                );
            }
        }


        /*
         * Null is acceptable for non-required fields.
         */
        if (value == null) {

            return;
        }


        String stringValue =
                String.valueOf(value);


        /*
         * Data type validation.
         */
        validateDataType(
                fieldId,
                stringValue,
                definition
        );


        /*
         * Length validation.
         */
        validateLength(
                fieldId,
                stringValue,
                definition
        );
    }


    /*
     * =========================================================
     * DATA TYPE VALIDATION
     * =========================================================
     */

    private void validateDataType(
            String fieldId,
            String value,
            CanonicalFieldDefinition definition) {

        if (definition.getType() == null) {

            return;
        }

        DataType type =
                definition.getType();

        switch (type) {

            case STRING:

                return;


            case INTEGER:

                try {

                    Integer.parseInt(value);

                } catch (NumberFormatException e) {

                    throw new IllegalArgumentException(
                            "Invalid INTEGER value for "
                                    + fieldId
                                    + ": "
                                    + value,
                            e
                    );
                }

                return;


            case LONG:

                try {

                    Long.parseLong(value);

                } catch (NumberFormatException e) {

                    throw new IllegalArgumentException(
                            "Invalid LONG value for "
                                    + fieldId
                                    + ": "
                                    + value,
                            e
                    );
                }

                return;


            case DECIMAL:

                try {

                    new java.math.BigDecimal(
                            value
                    );

                } catch (NumberFormatException e) {

                    throw new IllegalArgumentException(
                            "Invalid DECIMAL value for "
                                    + fieldId
                                    + ": "
                                    + value,
                            e
                    );
                }

                return;


            case BOOLEAN:

                if (!"true".equalsIgnoreCase(value)
                        && !"false".equalsIgnoreCase(value)) {

                    throw new IllegalArgumentException(
                            "Invalid BOOLEAN value for "
                                    + fieldId
                                    + ": "
                                    + value
                    );
                }

                return;


            default:

                throw new IllegalArgumentException(
                        "Unsupported canonical field type '"
                                + definition.getType()
                                + "' for "
                                + fieldId
                );
        }
    }


    /*
     * =========================================================
     * LENGTH VALIDATION
     * =========================================================
     */

    private void validateLength(
            String fieldId,
            String value,
            CanonicalFieldDefinition definition) {

        int length =
                value.length();


        if (definition.getLength() != null) {

            if (length !=
                    definition.getLength()) {

                throw new IllegalArgumentException(
                        "Invalid length for "
                                + fieldId
                                + ". Expected: "
                                + definition.getLength()
                                + ", Actual: "
                                + length
                );
            }
        }


        if (definition.getMaxLength() != null) {

            if (length >
                    definition.getMaxLength()) {

                throw new IllegalArgumentException(
                        "Value exceeds max length for "
                                + fieldId
                                + ". Maximum: "
                                + definition.getMaxLength()
                                + ", Actual: "
                                + length
                );
            }
        }
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

        if (id.startsWith("TF")) {

            return id;
        }

        return "TF" + id;
    }


    private String removeTFPrefix(
            String fieldId) {

        if (fieldId.startsWith("TF")) {

            return fieldId.substring(2);
        }

        return fieldId;
    }


    /*
     * =========================================================
     * MASKING
     * =========================================================
     */

    private String mask(
            Object value,
            MaskingConfiguration masking) {

        if (value == null) {

            return null;
        }

        String input =
                String.valueOf(value);

        if (masking == null) {

            return "*".repeat(
                    input.length()
            );
        }

        int showFirst =
                Math.max(
                        0,
                        masking.getShowFirst()
                );

        int showLast =
                Math.max(
                        0,
                        masking.getShowLast()
                );

        String maskCharacter =
                masking.getMaskCharacter();

        if (maskCharacter == null ||
                maskCharacter.isEmpty()) {

            maskCharacter = "*";
        }


        if (showFirst + showLast >=
                input.length()) {

            return maskCharacter.repeat(
                    input.length()
            );
        }


        int maskedLength =
                input.length()
                        - showFirst
                        - showLast;


        String firstPart =
                input.substring(
                        0,
                        showFirst
                );


        String lastPart =
                showLast == 0
                        ? ""
                        : input.substring(
                                input.length()
                                        - showLast
                        );


        return firstPart
                + maskCharacter.repeat(
                        maskedLength
                )
                + lastPart;
    }
}