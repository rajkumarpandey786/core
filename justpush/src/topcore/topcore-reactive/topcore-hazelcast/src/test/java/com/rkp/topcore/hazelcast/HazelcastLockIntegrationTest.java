package com.rkp.topcore.hazelcast;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;

@SpringBootTest(
        classes = HazelcastTestApplication.class
)
class HazelcastLockIntegrationTest {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Test
    void shouldAcquireAndReleaseFencedLock() {

        FencedLock lock =
                hazelcastInstance
                        .getCPSubsystem()
                        .getLock(
                                "TOPCORE_TEST_LOCK"
                        );

        lock.lock();

        try {

            assertTrue(
                    lock.isLockedByCurrentThread()
            );

        } finally {

            lock.unlock();
        }
    }
}
