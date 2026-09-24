package com.rkp.topcore.hazelcast.core;

import java.util.concurrent.CompletionStage;

import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import reactor.core.publisher.Mono;

/**
 * Hazelcast implementation of the TopCore DistributedStore.
 */
@Component
public class HazelcastDistributedStore
        implements DistributedStore {

    private final HazelcastInstance hazelcastInstance;

    public HazelcastDistributedStore(
            HazelcastInstance hazelcastInstance) {

        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public <K, V> Mono<Void> put(
            String namespace,
            K key,
            V value) {

        IMap<K, V> map =
                hazelcastInstance.getMap(namespace);

        return Mono.fromFuture(
                map.setAsync(key, value)
                        .toCompletableFuture()
        );
    }

    @Override
    public <K, V> Mono<V> get(
            String namespace,
            K key,
            Class<V> valueType) {

        IMap<K, V> map =
                hazelcastInstance.getMap(namespace);

        return Mono.fromFuture(
                map.getAsync(key)
                        .toCompletableFuture()
        );
    }

    @Override
    public <K> Mono<Void> remove(
            String namespace,
            K key) {

        IMap<K, Object> map =
                hazelcastInstance.getMap(namespace);

        return Mono.fromFuture(
                map.removeAsync(key)
                        .toCompletableFuture()
        ).then();
    }

}
