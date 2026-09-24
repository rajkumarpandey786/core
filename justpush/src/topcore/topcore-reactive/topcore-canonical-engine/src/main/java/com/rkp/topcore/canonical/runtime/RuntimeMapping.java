package com.rkp.topcore.canonical.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rkp.topcore.canonical.mapping.FieldMapping;
import com.rkp.topcore.canonical.mapping.MappingConfiguration;
import com.rkp.topcore.canonical.mapping.MappingDirection;

public final class RuntimeMapping {

    private final String integrationId;

    private final MappingDirection direction;

    /**
     * Immutable list of compiled mapping rules.
     *
     * Each rule contains:
     *
     * source
     * target
     * masking
     *
     * Example:
     *
     * root.rec.Country  -> TF001  -> false
     * root.rec.CardNbr  -> TF009  -> true
     */
    private final List<RuntimeMappingRule> rules;

    /**
     * Maps one source field to one or more target fields.
     *
     * This is retained as a convenient lookup structure for
     * existing mapping engines.
     *
     * Example:
     *
     * root.rec.Country -> [TF001]
     * root.rec.CardNbr -> [TF009]
     */
    private final Map<String, List<String>> sourceToTargets;

    /**
     * Source fields configured for masking.
     *
     * This is a derived immutable lookup structure.
     */
    private final Set<String> maskedSources;


    public RuntimeMapping(
            MappingConfiguration configuration) {

        if (configuration == null) {
            throw new IllegalArgumentException(
                    "Mapping configuration cannot be null"
            );
        }

        if (configuration.getIntegrationId() == null
                || configuration.getIntegrationId().isBlank()) {

            throw new IllegalArgumentException(
                    "Mapping integrationId cannot be null or blank"
            );
        }

        if (configuration.getDirection() == null) {

            throw new IllegalArgumentException(
                    "Mapping direction cannot be null"
            );
        }

        this.integrationId =
                configuration.getIntegrationId().trim();

        this.direction =
                configuration.getDirection();


        /*
         * Runtime structures.
         */
        List<RuntimeMappingRule> runtimeRules =
                new ArrayList<>();

        Map<String, List<String>> mappings =
                new LinkedHashMap<>();

        Set<String> mappingPairs =
                new HashSet<>();

        Set<String> maskingSources =
                new HashSet<>();


        /*
         * Compile configuration into immutable
         * runtime mapping rules.
         */
        if (configuration.getMappings() != null) {

            for (FieldMapping mapping :
                    configuration.getMappings()) {

                if (mapping == null) {

                    throw new IllegalArgumentException(
                            "Mapping entry cannot be null"
                    );
                }


                String source =
                        mapping.getSource();

                String target =
                        mapping.getTarget();


                if (source == null
                        || source.isBlank()) {

                    throw new IllegalArgumentException(
                            "Mapping source cannot be null or blank"
                    );
                }


                if (target == null
                        || target.isBlank()) {

                    throw new IllegalArgumentException(
                            "Mapping target cannot be null or blank"
                    );
                }


                /*
                 * Normalize configuration values once
                 * during startup.
                 *
                 * We do not want to repeatedly trim strings
                 * during transaction processing.
                 */
                source = source.trim();
                target = target.trim();


                /*
                 * Source + target must be unique.
                 *
                 * Same source with different targets
                 * is allowed.
                 *
                 * Example:
                 *
                 * TF001 -> country
                 * TF001 -> transaction.country
                 *
                 * is allowed.
                 */
                String mappingPair =
                        source + "\u0000" + target;


                if (!mappingPairs.add(mappingPair)) {

                    throw new IllegalArgumentException(
                            "Duplicate mapping: "
                                    + source
                                    + " -> "
                                    + target
                    );
                }


                /*
                 * Create the runtime rule.
                 *
                 * RuntimeMappingRule is now the
                 * authoritative representation of the
                 * mapping configuration.
                 */
                RuntimeMappingRule rule =
                        new RuntimeMappingRule(
                                source,
                                target,
                                mapping.isMasking()
                        );

                runtimeRules.add(rule);


                /*
                 * Build source -> targets lookup.
                 */
                mappings
                        .computeIfAbsent(
                                source,
                                key -> new ArrayList<>()
                        )
                        .add(target);


                /*
                 * Build masking lookup.
                 *
                 * The masking value belongs to the
                 * individual mapping rule.
                 *
                 * We retain this set as a convenient
                 * lookup for existing callers.
                 */
                if (mapping.isMasking()) {

                    maskingSources.add(source);
                }
            }
        }


        /*
         * Make runtime rules immutable.
         */
        this.rules =
                List.copyOf(runtimeRules);


        /*
         * Make source-to-target mapping immutable.
         */
        Map<String, List<String>> immutableMappings =
                new LinkedHashMap<>();

        mappings.forEach(
                (source, targets) ->
                        immutableMappings.put(
                                source,
                                List.copyOf(targets)
                        )
        );

        this.sourceToTargets =
                Collections.unmodifiableMap(
                        immutableMappings
                );


        /*
         * Make masking configuration immutable.
         *
         * RuntimeMapping is created during configuration
         * loading and then shared by all transactions.
         *
         * Nothing should be modified during runtime.
         */
        this.maskedSources =
                Collections.unmodifiableSet(
                        new HashSet<>(
                                maskingSources
                        )
                );
    }


    /**
     * Returns the integration identifier.
     */
    public String getIntegrationId() {

        return integrationId;
    }


    /**
     * Returns the mapping direction.
     */
    public MappingDirection getDirection() {

        return direction;
    }


    /**
     * Returns all compiled runtime mapping rules.
     *
     * The returned list is immutable.
     */
    public List<RuntimeMappingRule> getRules() {

        return rules;
    }


    /**
     * Returns all target fields grouped by source field.
     *
     * Example:
     *
     * root.rec.Country -> [TF001]
     * root.rec.CardNbr -> [TF009]
     *
     * The returned map is immutable.
     */
    public Map<String, List<String>>
            getSourceToTargets() {

        return sourceToTargets;
    }


    /**
     * Returns all targets configured for
     * the supplied source field.
     *
     * Returns null when the source is not configured.
     */
    public List<String> getTargets(
            String source) {

        return sourceToTargets.get(source);
    }


    /**
     * Returns whether the supplied source field
     * has at least one masking-enabled mapping rule.
     *
     * Example:
     *
     * isMasked("root.rec.CardNbr")
     *
     * returns true when:
     *
     * "masking": true
     *
     * is configured for that source.
     */
    public boolean isMasked(
            String source) {

        if (source == null) {

            return false;
        }

        return maskedSources.contains(source);
    }


    /**
     * Returns all source fields that have
     * masking enabled.
     *
     * The returned set is immutable.
     */
    public Set<String> getMaskedSources() {

        return maskedSources;
    }


    /**
     * Returns the number of compiled mapping rules.
     *
     * This is intentionally based on rules rather than
     * unique source fields.
     *
     * Example:
     *
     * TF001 -> country
     * TF001 -> transaction.country
     *
     * contains two mapping rules.
     */
    public int ruleCount() {

        return rules.size();
    }


    /**
     * Returns the number of unique source fields.
     *
     * Example:
     *
     * TF001 -> country
     * TF001 -> transaction.country
     *
     * contains one unique source field.
     */
    public int size() {

        return sourceToTargets.size();
    }


    @Override
    public String toString() {

        return "RuntimeMapping{"
                + "integrationId='"
                + integrationId
                + '\''
                + ", direction="
                + direction
                + ", ruleCount="
                + rules.size()
                + '}';
    }
}
