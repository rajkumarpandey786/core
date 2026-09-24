package com.rkp.load;

import java.util.concurrent.atomic.AtomicLong;

public class MetricsCollector {

    private final AtomicLong sent = new AtomicLong();

    private final AtomicLong received = new AtomicLong();

    private final AtomicLong errors = new AtomicLong();

    private final AtomicLong totalLatency = new AtomicLong();

    private final AtomicLong maxLatency = new AtomicLong();

    public void sent() {

        sent.incrementAndGet();
    }

    public void received(long latencyMs) {

        received.incrementAndGet();

        totalLatency.addAndGet(latencyMs);

        maxLatency.accumulateAndGet(
                latencyMs,
                Math::max);
    }

    public void error() {

        errors.incrementAndGet();
    }

    public void printAndReset() {

        long s = sent.getAndSet(0);

        long r = received.getAndSet(0);

        long e = errors.getAndSet(0);

        long total = totalLatency.getAndSet(0);

        long max = maxLatency.getAndSet(0);

        long avg = r == 0 ? 0 : total / r;

        System.out.printf(
                "TPS Sent=%d Received=%d Errors=%d Avg=%dms Max=%dms%n",
                s,
                r,
                e,
                avg,
                max);
    }
}