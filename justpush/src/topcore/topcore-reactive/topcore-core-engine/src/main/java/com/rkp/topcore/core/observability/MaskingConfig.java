package com.rkp.topcore.core.observability;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "masking")
public class MaskingConfig {

    private boolean enabled = true;

    private Format xml = new Format();

    private Format json = new Format();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Format getXml() {
        return xml;
    }

    public void setXml(Format xml) {
        this.xml = xml;
    }

    public Format getJson() {
        return json;
    }

    public void setJson(Format json) {
        this.json = json;
    }

    public static class Format {

        private List<MaskingRule> fields = new ArrayList<>();

        public List<MaskingRule> getFields() {
            return fields;
        }

        public void setFields(List<MaskingRule> fields) {
            this.fields = fields;
        }
    }
}