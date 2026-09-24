package com.rkp.topcore.core.downstream;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.core.config.GatewayConfig;
import com.rkp.topcore.core.context.TopCoreContext;
import com.rkp.topcore.core.observability.GatewayLogger;
import com.rkp.topcore.core.observability.ReactorMdcBridge;
import com.rkp.topcore.plugin.api.downstream.DownstreamCanonicalProcessor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class DownstreamManager {

    private static final Logger log =
            GatewayLogger.getLogger(
                    DownstreamManager.class);

    private final GatewayConfig config;

    private final Map<String, DownstreamClient> clients =
            new LinkedHashMap<>();

    private final DownstreamCanonicalProcessorRegistry
            processorRegistry;

    public DownstreamManager(
            GatewayConfig config,
            List<DownstreamClient> downstreamClients,
            DownstreamCanonicalProcessorRegistry
                    processorRegistry) {

        if (config == null) {
            throw new IllegalArgumentException(
                    "GatewayConfig cannot be null");
        }

        if (processorRegistry == null) {
            throw new IllegalArgumentException(
                    "Downstream processor registry "
                            + "cannot be null");
        }

        this.config = config;
        this.processorRegistry =
                processorRegistry;

        if (downstreamClients != null) {

            for (DownstreamClient client :
                    downstreamClients) {

                if (client == null) {
                    continue;
                }

                String systemName =
                        client.integrationId();

                if (systemName == null ||
                        systemName.isBlank()) {

                    throw new IllegalArgumentException(
                            "Downstream client system name "
                                    + "cannot be null or blank");
                }

                String normalizedName =
                        systemName.trim();

                if (clients.containsKey(
                        normalizedName)) {

                    throw new IllegalArgumentException(
                            "Duplicate downstream client: "
                                    + normalizedName);
                }

                clients.put(
                        normalizedName,
                        client);
            }
        }

        GatewayLogger.info(
                log,
                "DownstreamManager initialized "
                        + "clients={} processors={}",
                clients.keySet(),
                processorRegistry
                        .getProcessors()
                        .keySet());
    }

    public Mono<Void> execute(
            TopCoreContext context) {

        if (context == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "TopCoreContext cannot be null"));
        }

        return Mono.deferContextual(
                reactorContext -> {

                    ReactorMdcBridge.apply(
                            reactorContext);

                    CanonicalDocument baseDocument =
                            context.getRequestCanonicalDocument();

                    if (baseDocument == null) {

                        GatewayLogger.error(
                                log,
                                "Request canonical document is missing "
                                        + "correlationId={}",
                                context.getCorrelationId());

                        return Mono.error(
                                new IllegalStateException(
                                        "Request canonical document is missing "
                                                + "for correlationId="
                                                + context.getCorrelationId()));
                    }

                    List<Mono<Void>> calls =
                            new ArrayList<>();

                    for (String system :
                            config.getDownstream()
                                    .getActive()) {

                        DownstreamClient client =
                                clients.get(system);

                        if (client == null) {

                            GatewayLogger.warn(
                                    log,
                                    "Configured downstream {} "
                                            + "has no client implementation",
                                    system);

                            continue;
                        }

                        CanonicalDocument downstreamDocument =
                                baseDocument.copy();

                        context.setDownstreamRequestCanonicalDocument(
                                system,
                                downstreamDocument);

                        GatewayLogger.debug(
                                log,
                                "Created isolated request canonical "
                                        + "document downstream={} "
                                        + "correlationId={}",
                                system,
                                context.getCorrelationId());

                        Mono<Void> call =
                                Mono.deferContextual(
                                        downstreamContext -> {

                                            ReactorMdcBridge.apply(
                                                    downstreamContext);

                                            context.downstream(system)
                                                    .markStart();

                                            GatewayLogger.debug(
                                                    log,
                                                    "Downstream {} execution "
                                                            + "started correlationId={}",
                                                    system,
                                                    context.getCorrelationId());

                                            CanonicalDocument document =
                                                    context
                                                            .getDownstreamRequestCanonicalDocument(
                                                                    system);

                                            if (document == null) {

                                                return Mono.error(
                                                        new IllegalStateException(
                                                                "Downstream request "
                                                                        + "canonical document "
                                                                        + "is missing "
                                                                        + "for system="
                                                                        + system
                                                                        + " correlationId="
                                                                        + context
                                                                                .getCorrelationId()));
                                            }

                                            DownstreamCanonicalProcessor processor =
                                                    processorRegistry.get(system);

                                            if (processor != null) {

                                                GatewayLogger.debug(
                                                        log,
                                                        "Applying downstream "
                                                                + "canonical processor "
                                                                + "system={} "
                                                                + "correlationId={}",
                                                        system,
                                                        context
                                                                .getCorrelationId());

                                                CanonicalDocument processedDocument =
                                                        processor.process(
                                                                document);

                                                if (processedDocument == null) {

                                                    return Mono.error(
                                                            new IllegalStateException(
                                                                    "Downstream canonical "
                                                                            + "processor returned "
                                                                            + "null for system="
                                                                            + system));
                                                }

                                                context
                                                        .setDownstreamRequestCanonicalDocument(
                                                                system,
                                                                processedDocument);
                                            }

                                            return client.call(context)

                                                    .doOnSuccess(
                                                            ignored -> {

                                                                ReactorMdcBridge.apply(
                                                                        downstreamContext);

                                                                context.downstream(
                                                                                system)
                                                                        .markEnd();

                                                                GatewayLogger.info(
                                                                        log,
                                                                        "Downstream {} completed "
                                                                                + "responseCanonicalDocument={} "
                                                                                + "latency={}ms "
                                                                                + "correlationId={}",
                                                                        system,
                                                                        context
                                                                                .hasDownstreamResponseCanonicalDocument(
                                                                                        system),
                                                                        context
                                                                                .downstream(system)
                                                                                .latencyMillis(),
                                                                        context
                                                                                .getCorrelationId());
                                                            })

                                                    .doOnError(
                                                            ex -> {

                                                                ReactorMdcBridge.apply(
                                                                        downstreamContext);

                                                                context.downstream(
                                                                                system)
                                                                        .markEnd();

                                                                GatewayLogger.error(
                                                                        log,
                                                                        "Downstream {} failed "
                                                                                + "type={} message={} "
                                                                                + "latency={}ms "
                                                                                + "correlationId={}",
                                                                        system,
                                                                        ex.getClass()
                                                                                .getSimpleName(),
                                                                        ex.getMessage(),
                                                                        context
                                                                                .downstream(system)
                                                                                .latencyMillis(),
                                                                        context
                                                                                .getCorrelationId());
                                                            })

                                                    .onErrorResume(
                                                            ex -> {

                                                                ReactorMdcBridge.apply(
                                                                        downstreamContext);

                                                                GatewayLogger.warn(
                                                                        log,
                                                                        "Downstream {} failure "
                                                                                + "handled gracefully "
                                                                                + "type={} message={} "
                                                                                + "correlationId={}",
                                                                        system,
                                                                        ex.getClass()
                                                                                .getSimpleName(),
                                                                        ex.getMessage(),
                                                                        context
                                                                                .getCorrelationId());

                                                                return Mono.empty();
                                                            });
                                        });

                        calls.add(call);
                    }

                    GatewayLogger.info(
                            log,
                            "Executing {} downstream systems {} "
                                    + "correlationId={}",
                            calls.size(),
                            config.getDownstream()
                                    .getActive(),
                            context.getCorrelationId());

                    return Flux.merge(calls)
                            .then()
                            .doOnSuccess(
                                    ignored -> {

                                        ReactorMdcBridge.apply(
                                                reactorContext);

                                        GatewayLogger.info(
                                                log,
                                                "All downstream systems "
                                                        + "completed "
                                                        + "responseCanonicalDocuments={} "
                                                        + "correlationId={}",
                                                context
                                                        .getDownstreamResponseCanonicalDocuments()
                                                        .keySet(),
                                                context
                                                        .getCorrelationId());
                                    });
                });
    }
}