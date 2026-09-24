package com.rkp.topcore.hazelcast.core;

import reactor.core.publisher.Mono;

/**
 * Technology-independent distributed key-value store.
 *
 * TopCore business logic should depend on this abstraction
 * rather than directly depending on Hazelcast APIs.
 */
public interface DistributedStore {

    <K, V> Mono<Void> put(
            String namespace,
            K key,
            V value
    );

    <K, V> Mono<V> get(
            String namespace,
            K key,
            Class<V> valueType
    );

    <K> Mono<Void> remove(
            String namespace,
            K key
    );

}
