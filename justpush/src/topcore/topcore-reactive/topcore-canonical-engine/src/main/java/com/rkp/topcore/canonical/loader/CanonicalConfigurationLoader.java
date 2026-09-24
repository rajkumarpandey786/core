package com.rkp.topcore.canonical.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkp.topcore.canonical.definition.CanonicalConfiguration;
import com.rkp.topcore.canonical.definition.CanonicalFieldDefinition;
import com.rkp.topcore.canonical.definition.CanonicalConfiguration.ConfigurationSettings;

@Component
public class CanonicalConfigurationLoader {

    private final ObjectMapper objectMapper;


    public CanonicalConfigurationLoader(
            ObjectMapper objectMapper) {

        if (objectMapper == null) {

            throw new IllegalArgumentException(
                    "ObjectMapper cannot be null"
            );
        }

        this.objectMapper = objectMapper;

        this.objectMapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                true
        );
    }


    public Map<String, CanonicalFieldDefinition>
            loadInboundFields(
                    InputStream inputStream) {

        return loadFields(
                inputStream,
                "inbound"
        );
    }


    public Map<String, CanonicalFieldDefinition>
            loadOutboundFields(
                    InputStream inputStream) {

        return loadFields(
                inputStream,
                "outbound"
        );
    }


    private Map<String, CanonicalFieldDefinition>
            loadFields(
                    InputStream inputStream,
                    String direction) {

        if (inputStream == null) {

            throw new IllegalArgumentException(
                    "Canonical "
                            + direction
                            + " field configuration "
                            + "input stream cannot be null"
            );
        }

        try {

            CanonicalFieldConfiguration configuration =
                    objectMapper.readValue(
                            inputStream,
                            CanonicalFieldConfiguration.class
                    );

            if (configuration.getCanonicalFields() == null) {

                return new LinkedHashMap<>();
            }

            return new LinkedHashMap<>(
                    configuration.getCanonicalFields()
            );

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Unable to load TopCore canonical "
                            + direction
                            + " field configuration",
                    e
            );
        }
    }


    private static class CanonicalFieldConfiguration {

        private CanonicalConfiguration.ConfigurationSettings
                settings =
                new CanonicalConfiguration.ConfigurationSettings();

        private Map<String, CanonicalFieldDefinition>
                canonicalFields =
                new LinkedHashMap<>();


        public CanonicalConfiguration.ConfigurationSettings
                getSettings() {

            return settings;
        }


        public void setSettings(
                CanonicalConfiguration.ConfigurationSettings
                        settings) {

            this.settings = settings;
        }


        public Map<String, CanonicalFieldDefinition>
                getCanonicalFields() {

            return canonicalFields;
        }


        public void setCanonicalFields(
                Map<String, CanonicalFieldDefinition>
                        canonicalFields) {

            this.canonicalFields =
                    canonicalFields;
        }
    }
}