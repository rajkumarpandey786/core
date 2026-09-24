package com.rkp.topcore.canonical.mapping;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.rkp.topcore.canonical.runtime.RuntimeMapping;

/**
 * Runtime registry of all inbound and outbound mappings.
 *
 * This class is immutable after startup registration.
 * No file parsing happens here.
 */
@Component
public final class CanonicalMappingRegistry {

    /**
     * Key format:
     *
     * B-200:INBOUND
     * TSAS:OUTBOUND
     */
    private final Map<String, RuntimeMapping> registry =
            new LinkedHashMap<>();


    /**
     * Register a validated runtime mapping.
     */
    public void register(RuntimeMapping mapping) {

        if (mapping == null) {
            throw new IllegalArgumentException(
                    "RuntimeMapping cannot be null");
        }

        String key = buildKey(
                mapping.getIntegrationId(),
                mapping.getDirection());

        if (registry.containsKey(key)) {
            throw new IllegalStateException(
                    "Duplicate runtime mapping: " + key);
        }

        registry.put(key, mapping);
    }


    /**
     * Get an inbound mapping.
     */
    public RuntimeMapping getInbound(
            String integrationId) {

        return get(
                integrationId,
                MappingDirection.INBOUND);
    }


    /**
     * Get an outbound mapping.
     */
    public RuntimeMapping getOutbound(
            String integrationId) {

        return get(
                integrationId,
                MappingDirection.OUTBOUND);
    }


    /**
     * Generic lookup.
     */
    public RuntimeMapping get(
            String integrationId,
            MappingDirection direction) {

        if (integrationId == null ||
                integrationId.isBlank()) {

            throw new IllegalArgumentException(
                    "Integration ID cannot be null");
        }

        if (direction == null) {

            throw new IllegalArgumentException(
                    "Mapping direction cannot be null");
        }

        return registry.get(
                buildKey(integrationId, direction));
    }


    /**
     * Check whether a mapping exists.
     */
    public boolean contains(
            String integrationId,
            MappingDirection direction) {

        return get(integrationId, direction) != null;
    }


    /**
     * Number of registered mappings.
     */
    public int size() {

        return registry.size();
    }


    /**
     * List all registered keys.
     */
    public Set<String> keys() {

        return Collections.unmodifiableSet(
                registry.keySet());
    }


    /**
     * Read-only registry.
     */
    public Map<String, RuntimeMapping> getAll() {

        return Collections.unmodifiableMap(registry);
    }


    private String buildKey(
            String integrationId,
            MappingDirection direction) {

        return integrationId
                .trim()
                .toUpperCase()
                + ":"
                + direction.name();
    }
}