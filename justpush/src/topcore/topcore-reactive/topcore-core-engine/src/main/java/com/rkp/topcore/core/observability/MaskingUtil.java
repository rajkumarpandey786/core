package com.rkp.topcore.core.observability;

import org.springframework.stereotype.Component;

@Component
public class MaskingUtil {

    private final MaskingConfig config;

    public MaskingUtil(MaskingConfig config) {
        this.config = config;
    }

    public String maskXml(String xml) {

        if (!config.isEnabled() || xml == null) {
            return xml;
        }

        return XmlMasker.mask(xml, config.getXml().getFields());
    }

    public String maskJson(String json) {

        if (!config.isEnabled() || json == null) {
            return json;
        }

        return JsonMasker.mask(json, config.getJson().getFields());
    }

    public static String applyMask(String value,
                                   String type) {

        if (value == null) {
            return null;
        }

        switch (type.toUpperCase()) {

            case "PAN":
                if (value.length() <= 10) {
                    return "****";
                }

                return value.substring(0, 6)
                        + "******"
                        + value.substring(value.length() - 4);

            case "KEY":
                if (value.length() <= 8) {
                    return "****";
                }

                return value.substring(0, 4)
                        + "********"
                        + value.substring(value.length() - 4);

            case "AADHAAR":
                if (value.length() <= 4) {
                    return "****";
                }

                return "XXXX-XXXX-"
                        + value.substring(value.length() - 4);

            case "HIDE":
                return "****";

            default:
                return "****";
        }
    }
}