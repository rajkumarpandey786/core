package com.rkp.topcore.core.orchestration;

import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.engine.CanonicalEngine;
import com.rkp.topcore.core.application.ApplicationRequestProcessor;
import com.rkp.topcore.core.application.ApplicationResponseProcessor;
import com.rkp.topcore.core.config.GatewayConfig;
import com.rkp.topcore.core.context.TopCoreContext;
import com.rkp.topcore.core.database.model.MessageDirection;
import com.rkp.topcore.core.database.model.MessageStatus;
import com.rkp.topcore.core.database.model.TransactionStatus;
import com.rkp.topcore.core.database.persistence.MessagePersistenceCoordinator;
import com.rkp.topcore.core.database.persistence.TransactionPersistenceCoordinator;
import com.rkp.topcore.core.downstream.DownstreamManager;
import com.rkp.topcore.core.observability.GatewayLogger;
import com.rkp.topcore.core.observability.PerformanceLogger;
import com.rkp.topcore.core.observability.ReactorMdcBridge;
import com.rkp.topcore.core.plugin.UpstreamPluginRegistry;
import com.rkp.topcore.core.response.CommonResponseInitializer;
import com.rkp.topcore.core.xml.XmlCodec;
import com.rkp.topcore.plugin.api.upstream.UpstreamPlugin;

import reactor.core.publisher.Mono;

@Component
public class TransactionOrchestrator {

    private static final Logger log =
            GatewayLogger.getLogger(
                    TransactionOrchestrator.class);

    private final GatewayConfig config;

    private final DownstreamManager downstreamManager;

    private final CommonResponseInitializer
            commonResponseInitializer;

    private final CanonicalEngine canonicalEngine;

    private final XmlCodec codec;

    private final PerformanceLogger performanceLogger;

    private final ApplicationRequestProcessor
            applicationRequestProcessor;

    private final ApplicationResponseProcessor
            applicationResponseProcessor;

    private final TransactionPersistenceCoordinator
            transactionPersistenceCoordinator;

    private final MessagePersistenceCoordinator
            messagePersistenceCoordinator;

    private final UpstreamPluginRegistry
            upstreamPluginRegistry;

    public TransactionOrchestrator(
            GatewayConfig config,
            DownstreamManager downstreamManager,
            CommonResponseInitializer commonResponseInitializer,
            CanonicalEngine canonicalEngine,
            XmlCodec codec,
            PerformanceLogger performanceLogger,
            ApplicationRequestProcessor applicationRequestProcessor,
            ApplicationResponseProcessor applicationResponseProcessor,
            TransactionPersistenceCoordinator transactionPersistenceCoordinator,
            MessagePersistenceCoordinator messagePersistenceCoordinator,
            UpstreamPluginRegistry upstreamPluginRegistry) {

        if (config == null) {
            throw new IllegalArgumentException(
                    "GatewayConfig cannot be null");
        }

        if (downstreamManager == null) {
            throw new IllegalArgumentException(
                    "DownstreamManager cannot be null");
        }

        if (commonResponseInitializer == null) {
            throw new IllegalArgumentException(
                    "CommonResponseInitializer cannot be null");
        }

        if (canonicalEngine == null) {
            throw new IllegalArgumentException(
                    "CanonicalEngine cannot be null");
        }

        if (codec == null) {
            throw new IllegalArgumentException(
                    "XmlCodec cannot be null");
        }

        if (performanceLogger == null) {
            throw new IllegalArgumentException(
                    "PerformanceLogger cannot be null");
        }

        if (applicationRequestProcessor == null) {
            throw new IllegalArgumentException(
                    "ApplicationRequestProcessor cannot be null");
        }

        if (applicationResponseProcessor == null) {
            throw new IllegalArgumentException(
                    "ApplicationResponseProcessor cannot be null");
        }

        if (transactionPersistenceCoordinator == null) {
            throw new IllegalArgumentException(
                    "TransactionPersistenceCoordinator cannot be null");
        }

        if (messagePersistenceCoordinator == null) {
            throw new IllegalArgumentException(
                    "MessagePersistenceCoordinator cannot be null");
        }

        if (upstreamPluginRegistry == null) {
            throw new IllegalArgumentException(
                    "UpstreamPluginRegistry cannot be null");
        }

        this.config = config;
        this.downstreamManager = downstreamManager;
        this.commonResponseInitializer =
                commonResponseInitializer;
        this.canonicalEngine = canonicalEngine;
        this.codec = codec;
        this.performanceLogger = performanceLogger;
        this.applicationRequestProcessor =
                applicationRequestProcessor;
        this.applicationResponseProcessor =
                applicationResponseProcessor;
        this.transactionPersistenceCoordinator =
                transactionPersistenceCoordinator;
        this.messagePersistenceCoordinator =
                messagePersistenceCoordinator;
        this.upstreamPluginRegistry =
                upstreamPluginRegistry;
    }

    public Mono<String> process(
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

                    GatewayLogger.info(
                            log,
                            "Transaction orchestration started "
                                    + "correlationId={}",
                            context.getCorrelationId());

                    CanonicalDocument requestDocument =
                            context.getRequestCanonicalDocument();

                    if (requestDocument == null) {

                        GatewayLogger.error(
                                log,
                                "Request canonical document is missing "
                                        + "correlationId={}",
                                context.getCorrelationId());

                        return Mono.error(
                                new IllegalStateException(
                                        "Request canonical document "
                                                + "is missing for "
                                                + "correlationId="
                                                + context.getCorrelationId()));
                    }

                    return transactionPersistenceCoordinator
                            .start(
                                    context,
                                    context.getIntegrationId())

                            .doOnNext(
                                    databaseTransactionId ->
                                            GatewayLogger.info(
                                                    log,
                                                    "Database transaction "
                                                            + "created "
                                                            + "correlationId={} "
                                                            + "databaseTransactionId={}",
                                                    context.getCorrelationId(),
                                                    databaseTransactionId))

                            .then(
                                    messagePersistenceCoordinator.save(
                                            context,
                                            context.getIntegrationId(),
                                            MessageDirection.INBOUND,
                                            "REQUEST",
                                            context.getRequestPayload(),
                                            context.getMaskedRequestPayload(),
                                            MessageStatus.SUCCESS,
                                            null))

                            .doOnSuccess(
                                    ignored ->
                                            GatewayLogger.info(
                                                    log,
                                                    "C400 inbound message persisted "
                                                            + "correlationId={} "
                                                            + "transactionId={}",
                                                    context.getCorrelationId(),
                                                    context.getDatabaseTransactionId()))

                            .doOnError(
                                    error ->
                                            GatewayLogger.error(
                                                    log,
                                                    "C400 inbound message persistence failed "
                                                            + "correlationId={} "
                                                            + "transactionId={} "
                                                            + "error={}",
                                                    context.getCorrelationId(),
                                                    context.getDatabaseTransactionId(),
                                                    error.getMessage(),
                                                    error))

                            .then(
                                    Mono.fromRunnable(
                                            () ->
                                                    applicationRequestProcessor
                                                            .process(context)))

                            .then(
                                    downstreamManager
                                            .execute(context))

                            .timeout(
                                    Duration.ofMillis(
                                            config
                                                    .getDownstream()
                                                    .getTransactionTimeoutMs()))

                            .then(
                                    Mono.deferContextual(
                                            responseContext -> {

                                                ReactorMdcBridge.apply(
                                                        responseContext);

                                                GatewayLogger.info(
                                                        log,
                                                        "Initializing common "
                                                                + "response CanonicalDocument "
                                                                + "correlationId={}",
                                                        context.getCorrelationId());

                                                CanonicalDocument
                                                        commonResponse =
                                                        commonResponseInitializer
                                                                .initialize(
                                                                        context);

                                                if (commonResponse == null) {

                                                    GatewayLogger.error(
                                                            log,
                                                            "Common response "
                                                                    + "CanonicalDocument "
                                                                    + "initialization "
                                                                    + "failed "
                                                                    + "correlationId={}",
                                                            context.getCorrelationId());

                                                    return Mono.error(
                                                            new IllegalStateException(
                                                                    "Unable to initialize "
                                                                            + "common response "
                                                                            + "CanonicalDocument"));
                                                }

                                                GatewayLogger.info(
                                                        log,
                                                        "Common response "
                                                                + "CanonicalDocument "
                                                                + "initialized "
                                                                + "correlationId={}",
                                                        context.getCorrelationId());

                                                GatewayLogger.debug(
                                                        log,
                                                        "Common response before "
                                                                + "application response processing "
                                                                + "correlationId={} "
                                                                + "payload={}",
                                                        context.getCorrelationId(),
                                                        commonResponse);

                                                applicationResponseProcessor
                                                        .process(context);

                                                CanonicalDocument
                                                        processedResponse =
                                                        context
                                                                .getCommonResponseCanonicalDocument();

                                                GatewayLogger.info(
                                                        log,
                                                        "Application response "
                                                                + "processing completed "
                                                                + "correlationId={} "
                                                                + "payload={}",
                                                        context.getCorrelationId(),
                                                        processedResponse);

                                                return Mono.empty();
                                            }))

                            .then(
                                    Mono.deferContextual(
                                            responseContext -> {

                                                ReactorMdcBridge.apply(
                                                        responseContext);

                                                CanonicalDocument
                                                        finalResponse =
                                                        context
                                                                .getCommonResponseCanonicalDocument();

                                                if (finalResponse == null) {

                                                    GatewayLogger.error(
                                                            log,
                                                            "Common response "
                                                                    + "CanonicalDocument "
                                                                    + "is missing "
                                                                    + "correlationId={}",
                                                            context.getCorrelationId());

                                                    return Mono.error(
                                                            new IllegalStateException(
                                                                    "Common response "
                                                                            + "CanonicalDocument "
                                                                            + "is missing"));
                                                }

                                                GatewayLogger.debug(
                                                        log,
                                                        "Converting final common "
                                                                + "response CanonicalDocument "
                                                                + "to upstream response "
                                                                + "correlationId={}",
                                                        context.getCorrelationId());

                                                UpstreamPlugin
                                                        upstreamPlugin;

                                                try {

                                                    upstreamPlugin =
                                                            upstreamPluginRegistry
                                                                    .get(
                                                                            context
                                                                                    .getIntegrationId());

                                                } catch (Exception ex) {

                                                    GatewayLogger.error(
                                                            log,
                                                            "Unable to resolve upstream plugin "
                                                                    + "for response "
                                                                    + "correlationId={} "
                                                                    + "integrationId={}",
                                                            context.getCorrelationId(),
                                                            context.getIntegrationId(),
                                                            ex);

                                                    return Mono.error(ex);
                                                }

                                                String responsePayload;

                                                try {

                                                    responsePayload =
                                                            upstreamPlugin
                                                                    .fromCanonical(
                                                                            finalResponse);

                                                } catch (Exception ex) {

                                                    GatewayLogger.error(
                                                            log,
                                                            "Unable to convert response "
                                                                    + "CanonicalDocument "
                                                                    + "to upstream response "
                                                                    + "correlationId={} "
                                                                    + "integrationId={}",
                                                            context.getCorrelationId(),
                                                            context.getIntegrationId(),
                                                            ex);

                                                    return Mono.error(ex);
                                                }

                                                String maskedResponsePayload =
                                                        responsePayload;

                                                return messagePersistenceCoordinator
                                                        .save(
                                                                context,
                                                                context.getIntegrationId(),
                                                                MessageDirection.OUTBOUND,
                                                                "RESPONSE",
                                                                responsePayload,
                                                                maskedResponsePayload,
                                                                MessageStatus.SUCCESS,
                                                                null)

                                                        .doOnSuccess(
                                                                ignored ->
                                                                        GatewayLogger.info(
                                                                                log,
                                                                                "C400 outbound message "
                                                                                        + "persisted "
                                                                                        + "correlationId={} "
                                                                                        + "transactionId={}",
                                                                                context.getCorrelationId(),
                                                                                context.getDatabaseTransactionId()))

                                                        .then(
                                                                Mono.defer(
                                                                        () -> {

                                                                            context.markGatewayCompleted();

                                                                            long gatewayLatencyMs =
                                                                                    context.gatewayLatencyMillis();

                                                                            GatewayLogger.info(
                                                                                    log,
                                                                                    "Gateway transaction "
                                                                                            + "completed "
                                                                                            + "correlationId={} "
                                                                                            + "transactionId={} "
                                                                                            + "gatewayMs={}",
                                                                                    context.getCorrelationId(),
                                                                                    context.getDatabaseTransactionId(),
                                                                                    gatewayLatencyMs);

                                                                            return transactionPersistenceCoordinator
                                                                                    .complete(
                                                                                            context,
                                                                                            TransactionStatus.COMPLETED)

                                                                                    .doOnSuccess(
                                                                                            ignored ->
                                                                                                    GatewayLogger.info(
                                                                                                            log,
                                                                                                            "Database transaction "
                                                                                                                    + "completed "
                                                                                                                    + "correlationId={} "
                                                                                                                    + "transactionId={} "
                                                                                                                    + "gatewayMs={}",
                                                                                                            context.getCorrelationId(),
                                                                                                            context.getDatabaseTransactionId(),
                                                                                                            gatewayLatencyMs));
                                                                        }))

                                                        .thenReturn(
                                                                responsePayload);
                                            }));
                });
    }
}