package com.rkp.topcore.canonical.configuration;

import java.io.InputStream;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.rkp.topcore.canonical.mapping.CanonicalMappingRegistry;
import com.rkp.topcore.canonical.mapping.MappingConfiguration;
import com.rkp.topcore.canonical.mapping.MappingConfigurationLoader;
import com.rkp.topcore.canonical.mapping.MappingDirection;
import com.rkp.topcore.canonical.runtime.RuntimeMapping;

import jakarta.annotation.PostConstruct;

/**
 * Initializes all configured TopCore integration mappings
 * during application startup.
 *
 * Responsibility:
 *
 * application.yml
 *      ↓
 * mapping file locations
 *      ↓
 * *.pan mapping files
 *      ↓
 * RuntimeMapping
 *      ↓
 * CanonicalMappingRegistry
 *
 * Canonical field configuration (TC-Fields.pan) is NOT
 * loaded here. It is provided as the shared
 * CanonicalConfiguration Spring bean.
 */
@Component
public final class CanonicalMappingInitializer {

    private final CanonicalProperties properties;

    private final ResourceLoader resourceLoader;

    private final MappingConfigurationLoader
            mappingConfigurationLoader;

    private final CanonicalMappingRegistry registry;


    public CanonicalMappingInitializer(
            CanonicalProperties properties,
            ResourceLoader resourceLoader,
            MappingConfigurationLoader
                    mappingConfigurationLoader,
            CanonicalMappingRegistry registry) {

        if (properties == null) {

            throw new IllegalArgumentException(
                    "CanonicalProperties cannot be null"
            );
        }

        if (resourceLoader == null) {

            throw new IllegalArgumentException(
                    "ResourceLoader cannot be null"
            );
        }

        if (mappingConfigurationLoader == null) {

            throw new IllegalArgumentException(
                    "MappingConfigurationLoader "
                            + "cannot be null"
            );
        }

        if (registry == null) {

            throw new IllegalArgumentException(
                    "CanonicalMappingRegistry "
                            + "cannot be null"
            );
        }


        this.properties =
                properties;

        this.resourceLoader =
                resourceLoader;

        this.mappingConfigurationLoader =
                mappingConfigurationLoader;

        this.registry =
                registry;
    }


    /**
     * Initialize all integration mappings during
     * TopCore startup.
     */
    @PostConstruct
    public void initialize() {

        if (!properties.isEnabled()) {

            return;
        }

        loadIntegrationMappings();
    }


    /**
     * Returns the runtime mapping registry.
     *
     * The registry is populated once during startup
     * and is read during transaction processing.
     */
    public CanonicalMappingRegistry getRegistry() {

        return registry;
    }


    /**
     * Load all configured integration mappings.
     */
    private void loadIntegrationMappings() {

        Map<String,
                CanonicalProperties.IntegrationMapping>
                mappings =
                properties.getMappings();


        if (mappings == null
                || mappings.isEmpty()) {

            return;
        }


        for (Map.Entry<String,
                CanonicalProperties.IntegrationMapping>
                entry : mappings.entrySet()) {

            String integrationId =
                    entry.getKey();

            CanonicalProperties.IntegrationMapping
                    integration =
                    entry.getValue();


            if (integration == null) {

                throw new IllegalStateException(
                        "Mapping configuration is null "
                                + "for integration: "
                                + integrationId
                );
            }


            loadInboundMapping(
                    integrationId,
                    integration.getInbound()
            );


            loadOutboundMapping(
                    integrationId,
                    integration.getOutbound()
            );
        }
    }


    /**
     * Load one inbound mapping.
     */
    private void loadInboundMapping(
            String integrationId,
            String location) {

        if (location == null
                || location.isBlank()) {

            return;
        }


        RuntimeMapping runtimeMapping =
                loadMapping(
                        integrationId,
                        MappingDirection.INBOUND,
                        location
                );


        registry.register(
                runtimeMapping
        );
    }


    /**
     * Load one outbound mapping.
     */
    private void loadOutboundMapping(
            String integrationId,
            String location) {

        if (location == null
                || location.isBlank()) {

            return;
        }


        RuntimeMapping runtimeMapping =
                loadMapping(
                        integrationId,
                        MappingDirection.OUTBOUND,
                        location
                );


        registry.register(
                runtimeMapping
        );
    }


    /**
     * Load and validate one mapping file.
     */
    private RuntimeMapping loadMapping(
            String integrationId,
            MappingDirection direction,
            String location) {

        try {

            Resource resource =
                    resourceLoader.getResource(
                            location
                    );


            if (!resource.exists()) {

                throw new IllegalStateException(
                        "Mapping configuration does not "
                                + "exist: "
                                + location
                );
            }


            try (InputStream inputStream =
                         resource.getInputStream()) {

                MappingConfiguration configuration =
                        mappingConfigurationLoader.load(
                                inputStream
                        );


                /*
                 * Validate integration ID declared
                 * inside the .pan file against
                 * application.yml.
                 */
                if (!integrationId.equalsIgnoreCase(
                        configuration.getIntegrationId())) {

                    throw new IllegalStateException(
                            "Integration ID mismatch. "
                                    + "application.yml="
                                    + integrationId
                                    + ", mapping="
                                    + configuration
                                            .getIntegrationId()
                    );
                }


                /*
                 * Validate direction declared
                 * inside the .pan file.
                 */
                if (configuration.getDirection()
                        != direction) {

                    throw new IllegalStateException(
                            "Mapping direction mismatch "
                                    + "for integration "
                                    + integrationId
                                    + ". Expected="
                                    + direction
                                    + ", configured="
                                    + configuration
                                            .getDirection()
                    );
                }


                return new RuntimeMapping(
                        configuration
                );
            }


        } catch (Exception e) {

            throw new IllegalStateException(
                    "Unable to load "
                            + direction
                            + " mapping for "
                            + integrationId
                            + " from "
                            + location,
                    e
            );
        }
    }
}
