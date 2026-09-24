package com.rkp.topcore.canonical.configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "canonical")
public class CanonicalProperties {

    private boolean enabled = true;

    private Configuration configuration =
            new Configuration();

    private Map<String, IntegrationMapping> mappings =
            new LinkedHashMap<>();


    public boolean isEnabled() {

        return enabled;
    }


    public void setEnabled(
            boolean enabled) {

        this.enabled = enabled;
    }


    public Configuration getConfiguration() {

        return configuration;
    }


    public void setConfiguration(
            Configuration configuration) {

        if (configuration == null) {

            this.configuration =
                    new Configuration();

        } else {

            this.configuration =
                    configuration;
        }
    }


    public Map<String, IntegrationMapping>
            getMappings() {

        return mappings;
    }


    public void setMappings(
            Map<String, IntegrationMapping> mappings) {

        if (mappings == null) {

            this.mappings =
                    new LinkedHashMap<>();

        } else {

            this.mappings =
                    new LinkedHashMap<>(
                            mappings
                    );
        }
    }


    /*
     * =========================================================
     * CANONICAL FIELD CONFIGURATION
     * =========================================================
     *
     * Request-side and response-side canonical fields
     * are maintained independently.
     */

    public static class Configuration {

        private String inboundFields;

        private String outboundFields;


        public String getInboundFields() {

            return inboundFields;
        }


        public void setInboundFields(
                String inboundFields) {

            this.inboundFields =
                    inboundFields;
        }


        public String getOutboundFields() {

            return outboundFields;
        }


        public void setOutboundFields(
                String outboundFields) {

            this.outboundFields =
                    outboundFields;
        }
    }


    /*
     * =========================================================
     * INTEGRATION MAPPING
     * =========================================================
     */

    public static class IntegrationMapping {

        private String inbound;

        private String outbound;


        public String getInbound() {

            return inbound;
        }


        public void setInbound(
                String inbound) {

            this.inbound = inbound;
        }


        public String getOutbound() {

            return outbound;
        }


        public void setOutbound(
                String outbound) {

            this.outbound = outbound;
        }
    }
}