package com.rkp.topcore.canonical.mapping;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;

@Component
public class MappingConfigurationLoader {

    private final ObjectMapper objectMapper;

    public MappingConfigurationLoader() {

        this.objectMapper = new ObjectMapper();

        this.objectMapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                true
        );
    }

    public MappingConfiguration load(
            InputStream inputStream) {

        if (inputStream == null) {

            throw new IllegalArgumentException(
                    "Mapping configuration input stream cannot be null"
            );
        }

        try {

            return objectMapper.readValue(
                    inputStream,
                    MappingConfiguration.class
            );

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Unable to load TopCore mapping configuration",
                    e
            );
        }
    }
}