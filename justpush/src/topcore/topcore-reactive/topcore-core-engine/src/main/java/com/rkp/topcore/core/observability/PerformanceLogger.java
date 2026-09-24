package com.rkp.topcore.core.observability;

import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class PerformanceLogger {

    private static final Logger log =
            GatewayLogger.getLogger(PerformanceLogger.class);

    private final AtomicLong sent = new AtomicLong();

    private final AtomicLong completed = new AtomicLong();

    private final AtomicLong timedOut = new AtomicLong();

    private final AtomicLong failed = new AtomicLong();

    private final AtomicLong inflight = new AtomicLong();

    private final AtomicLong totalLatency = new AtomicLong();

    private final ConcurrentLinkedQueue<Long> latencySamples =
            new ConcurrentLinkedQueue<>();

    public void transactionStarted() {

        sent.incrementAndGet();

        inflight.incrementAndGet();
    }

    public void transactionCompleted(long latencyMs) {

        completed.incrementAndGet();

        inflight.decrementAndGet();

        totalLatency.addAndGet(latencyMs);

        latencySamples.offer(latencyMs);

        if (latencySamples.size() > 10000) {
            latencySamples.poll();
        }
    }

    public void transactionTimedOut(long latencyMs) {

        timedOut.incrementAndGet();

        inflight.decrementAndGet();

        totalLatency.addAndGet(latencyMs);

        latencySamples.offer(latencyMs);

        if (latencySamples.size() > 10000) {
            latencySamples.poll();
        }
    }

    public void transactionFailed() {

        failed.incrementAndGet();

        inflight.decrementAndGet();
    }

    @Scheduled(fixedRate = 1000)
    public void report() {

        long s = sent.getAndSet(0);

        long c = completed.getAndSet(0);

        long t = timedOut.getAndSet(0);

        long f = failed.getAndSet(0);

        long total = totalLatency.getAndSet(0);

        Long[] samples = latencySamples.toArray(new Long[0]);

        latencySamples.clear();

        long avg = (c + t) == 0
                ? 0
                : total / (c + t);

        long p50 = percentile(samples, 50);

        long p95 = percentile(samples, 95);

        long p99 = percentile(samples, 99);

        GatewayLogger.info(
                log,
                "PERF sent={} completed={} timeout={} failed={} inflight={} avg={}ms p50={}ms p95={}ms p99={}ms",
                s,
                c,
                t,
                f,
                inflight.get(),
                avg,
                p50,
                p95,
                p99);
    }

    private long percentile(Long[] values,
                            int percentile) {

        if (values.length == 0) {
            return 0;
        }

        Arrays.sort(values);

        int index = (int) Math.ceil(
                percentile / 100.0 * values.length) - 1;

        if (index < 0) {
            index = 0;
        }

        if (index >= values.length) {
            index = values.length - 1;
        }

        return values[index];
    }
}