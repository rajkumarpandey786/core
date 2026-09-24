package com.rkp.topcore.core.downstream;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.rkp.topcore.plugin.api.downstream.DownstreamCanonicalProcessor;

@Component
public class DownstreamCanonicalProcessorRegistry {

    private final Map<String, DownstreamCanonicalProcessor>
            processors =
                    new LinkedHashMap<>();


    public DownstreamCanonicalProcessorRegistry(
            List<DownstreamCanonicalProcessor>
                    downstreamProcessors) {

        if (downstreamProcessors == null) {
            return;
        }

        for (DownstreamCanonicalProcessor processor :
                downstreamProcessors) {

            if (processor == null) {
                continue;
            }

            String systemName =
                    processor.integrationId();

            if (systemName == null ||
                    systemName.isBlank()) {

                throw new IllegalArgumentException(
                        "Downstream processor system name "
                                + "cannot be null or blank"
                );
            }

            String normalizedName =
                    systemName.trim().toUpperCase();

            if (processors.containsKey(
                    normalizedName)) {

                throw new IllegalArgumentException(
                        "Duplicate downstream canonical "
                                + "processor: "
                                + normalizedName
                );
            }

            processors.put(
                    normalizedName,
                    processor
            );
        }
    }


    public DownstreamCanonicalProcessor get(
            String systemName) {

        if (systemName == null ||
                systemName.isBlank()) {

            return null;
        }

        return processors.get(
                systemName.trim().toUpperCase()
        );
    }


    public boolean contains(
            String systemName) {

        return get(systemName) != null;
    }


    public Map<String, DownstreamCanonicalProcessor>
            getProcessors() {

        return Map.copyOf(
                processors
        );
    }


    public int size() {

        return processors.size();
    }
}
