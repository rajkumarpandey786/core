package com.rkp.topcore.plugin.api.downstream;

import com.rkp.topcore.canonical.document.CanonicalDocument;

/**
 * Performs downstream-specific business transformation
 * on an isolated CanonicalDocument.
 *
 * The supplied document belongs exclusively to the
 * downstream system identified by systemName().
 */
public interface DownstreamCanonicalProcessor {

    /**
     * Downstream system handled by this processor.
     *
     * Examples:
     *
     * T
     * P
     */
    String integrationId();

    /**
     * Apply downstream-specific business changes.
     *
     * The framework guarantees that the supplied document
     * is an isolated copy of the base CanonicalDocument.
     *
     * The processor may therefore use:
     *
     * addTF()
     * removeTF()
     * overrideTF()
     *
     * according to the downstream business requirements.
     */
    CanonicalDocument process(
            CanonicalDocument document);
}
