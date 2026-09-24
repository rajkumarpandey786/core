package com.rkp.topcore.core.context;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import com.rkp.topcore.core.config.GatewayConfig;

@Component
public class ContextRegistry {

    private final GatewayConfig config;

    private final AtomicLong correlationGenerator =
            new AtomicLong();

    private final ConcurrentHashMap<
            Long,
            TopCoreContext> contexts =
            new ConcurrentHashMap<>();

    public ContextRegistry(
            GatewayConfig config) {

        if (config == null) {
            throw new IllegalArgumentException(
                    "GatewayConfig cannot be null");
        }

        this.config = config;
    }

    public TopCoreContext create(
            String integrationId,
            String sessionId,
            String clientMsgId,
            String requestPayload) {

        if (integrationId == null ||
                integrationId.isBlank()) {

            throw new IllegalArgumentException(
                    "integrationId cannot be null or blank");
        }

        if (contexts.size() >=
                config.getDownstream()
                        .getMaxInflightTransactions()) {

            throw new IllegalStateException(
                    "MAX_INFLIGHT_REACHED");
        }

        long correlationId =
                correlationGenerator
                        .incrementAndGet();

        TopCoreContext context =
                new TopCoreContext(
                        correlationId,
                        integrationId,
                        sessionId,
                        clientMsgId,
                        requestPayload);

        contexts.put(
                correlationId,
                context);

        return context;
    }

    public TopCoreContext get(
            long correlationId) {

        return contexts.get(
                correlationId);
    }

    public void remove(
            long correlationId) {

        contexts.remove(
                correlationId);
    }

    public int size() {
        return contexts.size();
    }

    public long getCurrentInflight() {
        return contexts.size();
    }

    public long getCorrelationCounter() {
        return correlationGenerator.get();
    }

    public void complete(
            long correlationId,
            String responsePayload) {

        TopCoreContext context =
                contexts.remove(
                        correlationId);

        if (context != null) {

            context.getCompletionSink()
                    .tryEmitValue(
                            responsePayload);
        }
    }

    public void fail(
            long correlationId,
            Throwable error) {

        TopCoreContext context =
                contexts.remove(
                        correlationId);

        if (context != null) {

            context.getCompletionSink()
                    .tryEmitError(
                            error);
        }
    }
}