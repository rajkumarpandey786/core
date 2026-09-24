package com.rkp.topcore.hazelcast.core;

import reactor.core.publisher.Mono;

public interface DistributedTokenStore {

    /**
     * Returns a valid shared access token.
     *
     * If a valid token already exists, it is returned immediately.
     *
     * If the token is missing or expired, the implementation
     * coordinates token refresh across TopCore instances so
     * that only one instance refreshes the token.
     */
    Mono<AccessToken> getValidToken(
            String tokenKey,
            TokenProvider tokenProvider
    );
}
