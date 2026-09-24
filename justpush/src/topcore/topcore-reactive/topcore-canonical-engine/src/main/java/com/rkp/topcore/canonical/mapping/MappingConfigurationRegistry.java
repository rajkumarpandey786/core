package com.rkp.topcore.canonical.mapping;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.rkp.topcore.canonical.runtime.RuntimeMapping;

public final class MappingConfigurationRegistry {

    private final Map<String, RuntimeMapping> mappings =
            new LinkedHashMap<>();


    public void register(
            RuntimeMapping runtimeMapping) {

        if (runtimeMapping == null) {

            throw new IllegalArgumentException(
                    "Runtime mapping cannot be null"
            );
        }

        String key =
                buildKey(
                        runtimeMapping.getIntegrationId(),
                        runtimeMapping.getDirection()
                );

        if (mappings.containsKey(key)) {

            throw new IllegalStateException(
                    "Runtime mapping already registered: "
                            + key
            );
        }

        mappings.put(
                key,
                runtimeMapping
        );
    }


    public RuntimeMapping get(
            String integrationId,
            MappingDirection direction) {

        if (integrationId == null
                || integrationId.isBlank()) {

            throw new IllegalArgumentException(
                    "Integration ID cannot be null or blank"
            );
        }

        if (direction == null) {

            throw new IllegalArgumentException(
                    "Mapping direction cannot be null"
            );
        }

        return mappings.get(
                buildKey(
                        integrationId,
                        direction
                )
        );
    }


    public boolean contains(
            String integrationId,
            MappingDirection direction) {

        return get(
                integrationId,
                direction
        ) != null;
    }


    public Map<String, RuntimeMapping>
            getAll() {

        return Collections.unmodifiableMap(
                mappings
        );
    }


    public int size() {

        return mappings.size();
    }


    private String buildKey(
            String integrationId,
            MappingDirection direction) {

        return integrationId.trim()
                .toUpperCase()
                + ":"
                + direction.name();
    }
}