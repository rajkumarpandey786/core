package com.rkp.topcore.hazelcast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.rkp.topcore.hazelcast.core.DistributedStore;

import reactor.test.StepVerifier;

@SpringBootTest(
        classes = HazelcastTestApplication.class
)
class HazelcastIntegrationTest {

    @Autowired
    private DistributedStore distributedStore;

    @Test
    void shouldPutAndGetValue() {

        String namespace = "TOPCORE_TEST";
        String key = "TXN-1001";
        String value = "SUCCESS";

        StepVerifier.create(
                distributedStore.put(
                        namespace,
                        key,
                        value
                )
        )
        .verifyComplete();

        StepVerifier.create(
                distributedStore.get(
                        namespace,
                        key,
                        String.class
                )
        )
        .assertNext(result ->
                assertEquals(value, result)
        )
        .verifyComplete();
    }

    @Test
    void shouldPutGetAndRemoveValue() {

        String namespace = "TOPCORE_TEST";
        String key = "TXN-1002";
        String value = "PROCESSING";

        // PUT
        StepVerifier.create(
                distributedStore.put(
                        namespace,
                        key,
                        value
                )
        )
        .verifyComplete();

        // GET
        StepVerifier.create(
                distributedStore.get(
                        namespace,
                        key,
                        String.class
                )
        )
        .assertNext(result ->
                assertEquals(value, result)
        )
        .verifyComplete();

        // REMOVE
        StepVerifier.create(
                distributedStore.remove(
                        namespace,
                        key
                )
        )
        .verifyComplete();

        // GET after REMOVE
        StepVerifier.create(
                distributedStore.get(
                        namespace,
                        key,
                        String.class
                )
        )
        .verifyComplete();
    }
}
