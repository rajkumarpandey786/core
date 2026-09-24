package com.rkp.topcore.hazelcast.core;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface TokenProvider {

    /**
     * Obtains a new access token from the downstream
     * authorization/token endpoint.
     *
     * This method is invoked only when the shared token
     * is missing or expired and the caller has acquired
     * the distributed lock.
     */
    Mono<AccessToken> refreshToken();
}
