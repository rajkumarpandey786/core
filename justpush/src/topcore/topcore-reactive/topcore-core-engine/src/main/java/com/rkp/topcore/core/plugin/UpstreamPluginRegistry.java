package com.rkp.topcore.core.plugin;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.rkp.topcore.plugin.api.upstream.UpstreamPlugin;

@Component
public class UpstreamPluginRegistry {

    private final Map<String, UpstreamPlugin> plugins;

    public UpstreamPluginRegistry(
            java.util.List<UpstreamPlugin> plugins) {

        if (plugins == null) {
            throw new IllegalArgumentException(
                    "Upstream plugins cannot be null");
        }

        this.plugins = plugins.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableMap(
                        UpstreamPlugin::integrationId,
                        Function.identity()));
    }

    public UpstreamPlugin get(
            String integrationId) {

        if (integrationId == null ||
                integrationId.isBlank()) {

            throw new IllegalArgumentException(
                    "IntegrationId cannot be null or blank");
        }

        UpstreamPlugin plugin =
                plugins.get(integrationId);

        if (plugin == null) {
            throw new IllegalArgumentException(
                    "No upstream plugin registered for integrationId: "
                            + integrationId);
        }

        return plugin;
    }

    public boolean contains(
            String integrationId) {

        return integrationId != null &&
                plugins.containsKey(integrationId);
    }

    public int size() {
        return plugins.size();
    }
}