package com.rkp.topcore.canonical.path;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class JsonPathWriter {

    /**
     * Writes a value into a nested JSON-style Map structure.
     *
     * Example:
     *
     * path  = "transaction.amount"
     * value = "23522.000"
     *
     * produces:
     *
     * {
     *     "transaction": {
     *         "amount": "23522.000"
     *     }
     * }
     *
     * The value may be any Java object, including:
     *
     * String
     * Number
     * Boolean
     * Map
     * List
     * List<Map<String, Object>>
     *
     * This allows the writer to support structured
     * JSON values without knowing anything about
     * TSAS, PAIMI, or any other downstream system.
     */
    public void write(
            Map<String, Object> root,
            String path,
            Object value) {

        if (root == null) {

            throw new IllegalArgumentException(
                    "Root map cannot be null"
            );
        }

        if (path == null || path.isBlank()) {

            throw new IllegalArgumentException(
                    "JSON target path cannot be blank"
            );
        }


        String normalizedPath =
                path.trim();


        String[] parts =
                normalizedPath.split("\\.", -1);


        /*
         * Validate every path component.
         *
         * This prevents malformed paths such as:
         *
         * transaction..amount
         * .transaction.amount
         * transaction.amount.
         */
        for (String part : parts) {

            if (part == null || part.isBlank()) {

                throw new IllegalArgumentException(
                        "Invalid JSON target path: "
                                + path
                );
            }
        }


        Map<String, Object> current =
                root;


        /*
         * Walk through all intermediate
         * JSON object components.
         */
        for (int i = 0;
             i < parts.length - 1;
             i++) {

            String part =
                    parts[i];


            Object existing =
                    current.get(part);


            if (existing == null) {

                /*
                 * The intermediate object does not
                 * exist yet, so create it.
                 */
                Map<String, Object> child =
                        new LinkedHashMap<>();

                current.put(
                        part,
                        child
                );

                current = child;

            } else if (existing instanceof Map<?, ?>) {

                /*
                 * The intermediate object already
                 * exists.
                 *
                 * Reuse it so multiple mappings such as:
                 *
                 * transaction.amount
                 * transaction.currency
                 * transaction.messageDirection
                 *
                 * all populate the same transaction object.
                 */
                @SuppressWarnings("unchecked")
                Map<String, Object> child =
                        (Map<String, Object>) existing;

                current = child;

            } else {

                /*
                 * An intermediate path component is
                 * already occupied by a scalar or some
                 * other incompatible object.
                 *
                 * Example:
                 *
                 * TF001 -> transaction
                 * TF002 -> transaction.amount
                 *
                 * If transaction already contains a
                 * String, transaction.amount cannot be
                 * created.
                 */
                throw new IllegalStateException(
                        "Cannot create JSON path '"
                                + normalizedPath
                                + "' because '"
                                + part
                                + "' is not an object"
                );
            }
        }


        /*
         * Write the final value.
         *
         * The value can be a scalar, Map, List,
         * or any structured object supported by
         * the JSON serializer.
         */
        current.put(
                parts[parts.length - 1],
                value
        );
    }
}
