package com.rkp.topcore.canonical.mapping;

import com.rkp.topcore.canonical.runtime.RuntimeMapping;

public final class MappingConfigurationResolver {

    private final MappingConfigurationRegistry registry;


    public MappingConfigurationResolver(
            MappingConfigurationRegistry registry) {

        if (registry == null) {

            throw new IllegalArgumentException(
                    "Mapping registry cannot be null"
            );
        }

        this.registry = registry;
    }


    public RuntimeMapping resolveInbound(
            String integrationId) {

        return resolve(
                integrationId,
                MappingDirection.INBOUND
        );
    }


    public RuntimeMapping resolveOutbound(
            String integrationId) {

        return resolve(
                integrationId,
                MappingDirection.OUTBOUND
        );
    }


    public RuntimeMapping resolve(
            String integrationId,
            MappingDirection direction) {

        RuntimeMapping mapping =
                registry.get(
                        integrationId,
                        direction
                );

        if (mapping == null) {

            throw new IllegalStateException(
                    "No runtime mapping configured for "
                            + integrationId
                            + " / "
                            + direction
            );
        }

        return mapping;
    }
}