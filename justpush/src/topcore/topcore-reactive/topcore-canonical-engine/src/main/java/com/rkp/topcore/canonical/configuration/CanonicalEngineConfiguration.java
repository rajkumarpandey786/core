package com.rkp.topcore.canonical.configuration;

import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkp.topcore.canonical.definition.CanonicalConfiguration;
import com.rkp.topcore.canonical.definition.CanonicalFieldDefinition;
import com.rkp.topcore.canonical.loader.CanonicalConfigurationLoader;

@Configuration
public class CanonicalEngineConfiguration {

    /*
     * =========================================================
     * SHARED OBJECT MAPPER
     * =========================================================
     *
     * One ObjectMapper instance is shared by the Canonical Engine.
     */
    @Bean
    public ObjectMapper canonicalObjectMapper() {

        return new ObjectMapper();
    }


    /*
     * =========================================================
     * CANONICAL CONFIGURATION
     * =========================================================
     *
     * Loads both:
     *
     * TC-Fields-Inbound.pan
     * TC-Fields-Outbound.pan
     *
     * once during application startup.
     *
     * The resulting CanonicalConfiguration is shared by
     * the entire application.
     */
    @Bean
    public CanonicalConfiguration canonicalConfiguration(

            CanonicalProperties properties,

            ResourceLoader resourceLoader,

            CanonicalConfigurationLoader loader) {

        String inboundLocation =
                properties
                        .getConfiguration()
                        .getInboundFields();

        String outboundLocation =
                properties
                        .getConfiguration()
                        .getOutboundFields();


        /*
         * ---------------------------------------------------------
         * Validate configuration
         * ---------------------------------------------------------
         */

        if (inboundLocation == null ||
                inboundLocation.isBlank()) {

            throw new IllegalStateException(
                    "Inbound canonical fields configuration "
                            + "location is not configured"
            );
        }


        if (outboundLocation == null ||
                outboundLocation.isBlank()) {

            throw new IllegalStateException(
                    "Outbound canonical fields configuration "
                            + "location is not configured"
            );
        }


        /*
         * ---------------------------------------------------------
         * Load inbound fields
         * ---------------------------------------------------------
         */

        var inboundFields =
                loadFields(
                        inboundLocation,
                        "inbound",
                        resourceLoader,
                        loader
                );


        /*
         * ---------------------------------------------------------
         * Load outbound fields
         * ---------------------------------------------------------
         */

        var outboundFields =
                loadFields(
                        outboundLocation,
                        "outbound",
                        resourceLoader,
                        loader
                );


        /*
         * ---------------------------------------------------------
         * Build combined runtime configuration
         * ---------------------------------------------------------
         */

        CanonicalConfiguration configuration =
                new CanonicalConfiguration();

        configuration.setInboundFields(
                inboundFields
        );

        configuration.setOutboundFields(
                outboundFields
        );


        return configuration;
    }


    /*
     * =========================================================
     * LOAD ONE FIELD CONFIGURATION
     * =========================================================
     */

    private java.util.Map<
            String,
            CanonicalFieldDefinition>
            loadFields(

                    String location,

                    String direction,

                    ResourceLoader resourceLoader,

                    CanonicalConfigurationLoader loader) {

        try {

            Resource resource =
                    resourceLoader.getResource(
                            location
                    );


            if (!resource.exists()) {

                throw new IllegalStateException(
                        "Canonical "
                                + direction
                                + " fields configuration "
                                + "does not exist: "
                                + location
                );
            }


            try (InputStream inputStream =
                         resource.getInputStream()) {

                if ("inbound".equals(direction)) {

                    return loader.loadInboundFields(
                            inputStream
                    );
                }


                return loader.loadOutboundFields(
                        inputStream
                );
            }

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Unable to load canonical "
                            + direction
                            + " fields configuration: "
                            + location,
                    e
            );
        }
    }
}