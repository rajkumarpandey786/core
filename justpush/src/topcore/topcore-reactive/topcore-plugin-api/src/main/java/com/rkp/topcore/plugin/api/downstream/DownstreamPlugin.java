package com.rkp.topcore.plugin.api.downstream;

import com.rkp.topcore.canonical.document.CanonicalDocument;
import reactor.core.publisher.Mono;

public interface DownstreamPlugin {

    String integrationId();

    Mono<CanonicalDocument> process(
            CanonicalDocument request);
}