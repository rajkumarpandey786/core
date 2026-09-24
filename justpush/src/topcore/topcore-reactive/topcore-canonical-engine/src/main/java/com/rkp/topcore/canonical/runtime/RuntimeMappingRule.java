package com.rkp.topcore.canonical.runtime;

/**
 * Immutable runtime representation of a single
 * canonical mapping rule.
 *
 * Example:
 *
 * source = TF001
 * target = country
 *
 * source = TF010
 * target = transaction.amount
 *
 * source = TF050
 * target = transaction.details
 *
 * The runtime rule is created from the
 * corresponding FieldMapping loaded from a .pan file.
 */
public final class RuntimeMappingRule {

    private final String source;
    private final String target;
    private final boolean masking;

    /**
     * Creates a runtime mapping rule.
     *
     * @param source canonical source field
     * @param target downstream target path
     * @param masking whether the source field is
     *                configured for masking
     */
    public RuntimeMappingRule(
            String source,
            String target,
            boolean masking) {

        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException(
                    "Mapping source cannot be null or blank");
        }

        if (target == null || target.isBlank()) {
            throw new IllegalArgumentException(
                    "Mapping target cannot be null or blank");
        }

        this.source = source.trim();
        this.target = target.trim();
        this.masking = masking;
    }

    /**
     * Returns the canonical source field.
     *
     * Example:
     * TF001
     */
    public String getSource() {
        return source;
    }

    /**
     * Returns the downstream target path.
     *
     * Examples:
     *
     * country
     * transaction.amount
     * transaction.currency
     * transaction.details
     */
    public String getTarget() {
        return target;
    }

    /**
     * Returns whether masking is configured
     * for this mapping.
     */
    public boolean isMasking() {
        return masking;
    }

    @Override
    public String toString() {
        return "RuntimeMappingRule{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", masking=" + masking +
                '}';
    }
}
