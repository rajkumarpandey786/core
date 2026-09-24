package com.rkp.topcore.core.downstream;

import com.rkp.topcore.core.context.TopCoreContext;

import reactor.core.publisher.Mono;

public interface DownstreamClient {

    String integrationId();

    Mono<Void> call(
            TopCoreContext context);
}