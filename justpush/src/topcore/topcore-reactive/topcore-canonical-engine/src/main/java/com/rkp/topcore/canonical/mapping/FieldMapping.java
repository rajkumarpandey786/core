package com.rkp.topcore.canonical.mapping;

public class FieldMapping {

    private String source;

    private String target;

    /*
     * Indicates whether the source field must be masked
     * when the message is written to logs.
     *
     * This flag belongs to the integration mapping
     * configuration, not to the canonical field definition.
     */
    private boolean masking;


    public String getSource() {
        return source;
    }

    public void setSource(
            String source) {

        this.source = source;
    }


    public String getTarget() {
        return target;
    }

    public void setTarget(
            String target) {

        this.target = target;
    }


    public boolean isMasking() {
        return masking;
    }

    public void setMasking(
            boolean masking) {

        this.masking = masking;
    }
}
