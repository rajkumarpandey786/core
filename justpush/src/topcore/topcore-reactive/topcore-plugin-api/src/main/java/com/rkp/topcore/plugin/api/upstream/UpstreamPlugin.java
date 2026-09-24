package com.rkp.topcore.plugin.api.upstream;

import com.rkp.topcore.canonical.document.CanonicalDocument;

public interface UpstreamPlugin {

    String integrationId();

    UpstreamRoute route(String requestPayload);

    UpstreamTransactionMetadata transactionMetadata(
            String requestPayload);

    CanonicalDocument toCanonical(
            String requestPayload);

    String fromCanonical(
            CanonicalDocument response);
}