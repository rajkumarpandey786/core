package com.rkp.topcore.hazelcast.core;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import com.hazelcast.map.IMap;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class HazelcastDistributedTokenStore
        implements DistributedTokenStore {

    private static final String TOKEN_MAP =
            "TOPCORE_ACCESS_TOKENS";

    private static final String LOCK_PREFIX =
            "TOPCORE_ACCESS_TOKEN_LOCK_";

    private final HazelcastInstance hazelcastInstance;

    public HazelcastDistributedTokenStore(
            HazelcastInstance hazelcastInstance) {

        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public Mono<AccessToken> getValidToken(
            String tokenKey,
            TokenProvider tokenProvider) {

        return getToken(tokenKey)
                .flatMap(existingToken -> {

                    if (existingToken.isValid(60)) {
                        return Mono.just(existingToken);
                    }

                    return refreshWithLock(
                            tokenKey,
                            tokenProvider
                    );
                })
                .switchIfEmpty(
                        refreshWithLock(
                                tokenKey,
                                tokenProvider
                        )
                );
    }

    private Mono<AccessToken> getToken(
            String tokenKey) {

        IMap<String, AccessToken> tokenMap =
                hazelcastInstance.getMap(TOKEN_MAP);

        return Mono.fromFuture(
                tokenMap.getAsync(tokenKey)
                        .toCompletableFuture()
        );
    }

    private Mono<AccessToken> refreshWithLock(
            String tokenKey,
            TokenProvider tokenProvider) {

        return Mono.usingWhen(
                Mono.fromCallable(() ->
                        hazelcastInstance
                                .getCPSubsystem()
                                .getLock(
                                        LOCK_PREFIX + tokenKey
                                )
                ),
                lock ->
                        acquireAndRefresh(
                                lock,
                                tokenKey,
                                tokenProvider
                        ),
                this::releaseLock,
                (lock, error) ->
                        releaseLock(lock),
                this::releaseLock
        );
    }

    private Mono<AccessToken> acquireAndRefresh(
            FencedLock lock,
            String tokenKey,
            TokenProvider tokenProvider) {

        return Mono.fromRunnable(lock::lock)
                .subscribeOn(Schedulers.boundedElastic())
                .then(
                        // IMPORTANT:
                        // Check the token again after acquiring
                        // the distributed lock.
                        getToken(tokenKey)
                                .flatMap(existingToken -> {

                                    if (existingToken.isValid(60)) {
                                        return Mono.just(existingToken);
                                    }

                                    return refreshAndStore(
                                            tokenKey,
                                            tokenProvider
                                    );
                                })
                                .switchIfEmpty(
                                        refreshAndStore(
                                                tokenKey,
                                                tokenProvider
                                        )
                                )
                );
    }

    private Mono<AccessToken> refreshAndStore(
            String tokenKey,
            TokenProvider tokenProvider) {

        return tokenProvider
                .refreshToken()
                .switchIfEmpty(
                        Mono.error(
                                new IllegalStateException(
                                        "TokenProvider returned empty token"
                                )
                        )
                )
                .flatMap(token -> {

                    if (token == null ||
                            token.value() == null ||
                            token.value().isBlank()) {

                        return Mono.error(
                                new IllegalStateException(
                                        "TokenProvider returned invalid token"
                                )
                        );
                    }

                    IMap<String, AccessToken> tokenMap =
                            hazelcastInstance.getMap(
                                    TOKEN_MAP
                            );

                    return Mono.fromFuture(
                            tokenMap.setAsync(
                                    tokenKey,
                                    token
                            ).toCompletableFuture()
                    ).thenReturn(token);
                });
    }

    private Mono<Void> releaseLock(
            FencedLock lock) {

        return Mono.fromRunnable(() -> {

            if (lock.isLockedByCurrentThread()) {
                lock.unlock();
            }

        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
