package com.rkp.topcore.core.observability;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public final class JsonMasker {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonMasker() {
    }

    public static String mask(String json,
                              List<MaskingRule> rules) {

        try {

            JsonNode root = MAPPER.readTree(json);

            for (MaskingRule rule : rules) {

                apply(root, rule);
            }

            return MAPPER.writeValueAsString(root);

        } catch (Exception ex) {

            return json;
        }
    }

    private static void apply(JsonNode root,
                              MaskingRule rule) {

        String[] parts = rule.getPath().split("\\\\.");

        JsonNode current = root;

        for (int i = 0; i < parts.length - 1; i++) {

            if (current == null || !current.isObject()) {
                return;
            }

            current = current.get(parts[i]);
        }

        if (current == null || !current.isObject()) {
            return;
        }

        String field = parts[parts.length - 1];

        JsonNode valueNode = current.get(field);

        if (valueNode == null || valueNode.isNull()) {
            return;
        }

        String masked =
                MaskingUtil.applyMask(
                        valueNode.asText(),
                        rule.getType());

        ((ObjectNode) current).put(field, masked);
    }
}