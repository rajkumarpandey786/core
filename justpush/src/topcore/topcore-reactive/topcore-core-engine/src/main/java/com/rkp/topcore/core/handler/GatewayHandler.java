package com.rkp.topcore.core.handler;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.mapping.CanonicalMappingRegistry;
import com.rkp.topcore.canonical.runtime.RuntimeMapping;
import com.rkp.topcore.core.config.GatewayConfig;
import com.rkp.topcore.core.context.ContextRegistry;
import com.rkp.topcore.core.context.TopCoreContext;
import com.rkp.topcore.core.observability.GatewayLogger;
import com.rkp.topcore.core.observability.LogContext;
import com.rkp.topcore.core.observability.LogContextThreadLocalAccessor;
import com.rkp.topcore.core.orchestration.TransactionOrchestrator;
import com.rkp.topcore.core.plugin.UpstreamPluginRegistry;
import com.rkp.topcore.core.security.XmlMaskingUtility;
import com.rkp.topcore.core.session.ClientSession;
import com.rkp.topcore.core.session.SessionManager;
import com.rkp.topcore.core.xml.XmlCodec;
import com.rkp.topcore.core.xml.XmlMessages;
import com.rkp.topcore.plugin.api.upstream.UpstreamPlugin;
import com.rkp.topcore.plugin.api.upstream.UpstreamRoute;
import com.rkp.topcore.plugin.api.upstream.UpstreamTransactionMetadata;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;

@Component
public class GatewayHandler {

    private static final Logger log =
            GatewayLogger.getLogger(
                    GatewayHandler.class);

    private static final AttributeKey<String>
            SESSION_ID =
            AttributeKey.valueOf(
                    "SESSION_ID");

    private final XmlCodec codec;
    private final SessionManager sessionManager;
    private final GatewayConfig gatewayConfig;
    private final UpstreamPluginRegistry upstreamPluginRegistry;
    private final ContextRegistry contextRegistry;
    private final TransactionOrchestrator
            transactionOrchestrator;
    private final CanonicalMappingRegistry
            canonicalMappingRegistry;
    private final XmlMaskingUtility
            xmlMaskingUtility;

    public GatewayHandler(
            XmlCodec codec,
            SessionManager sessionManager,
            GatewayConfig gatewayConfig,
            UpstreamPluginRegistry upstreamPluginRegistry,
            ContextRegistry contextRegistry,
            TransactionOrchestrator transactionOrchestrator,
            CanonicalMappingRegistry canonicalMappingRegistry,
            XmlMaskingUtility xmlMaskingUtility) {

        if (codec == null) {
            throw new IllegalArgumentException(
                    "XmlCodec cannot be null");
        }

        if (sessionManager == null) {
            throw new IllegalArgumentException(
                    "SessionManager cannot be null");
        }

        if (gatewayConfig == null) {
            throw new IllegalArgumentException(
                    "GatewayConfig cannot be null");
        }

        if (upstreamPluginRegistry == null) {
            throw new IllegalArgumentException(
                    "UpstreamPluginRegistry cannot be null");
        }

        if (contextRegistry == null) {
            throw new IllegalArgumentException(
                    "ContextRegistry cannot be null");
        }

        if (transactionOrchestrator == null) {
            throw new IllegalArgumentException(
                    "TransactionOrchestrator cannot be null");
        }

        if (canonicalMappingRegistry == null) {
            throw new IllegalArgumentException(
                    "CanonicalMappingRegistry cannot be null");
        }

        if (xmlMaskingUtility == null) {
            throw new IllegalArgumentException(
                    "XmlMaskingUtility cannot be null");
        }

        this.codec = codec;
        this.sessionManager = sessionManager;
        this.gatewayConfig = gatewayConfig;
        this.upstreamPluginRegistry =
                upstreamPluginRegistry;
        this.contextRegistry = contextRegistry;
        this.transactionOrchestrator =
                transactionOrchestrator;
        this.canonicalMappingRegistry =
                canonicalMappingRegistry;
        this.xmlMaskingUtility =
                xmlMaskingUtility;
    }

    public Mono<Void> handle(
            NettyInbound inbound,
            NettyOutbound outbound) {

        AtomicReference<Channel>
                channelRef =
                new AtomicReference<>();

        inbound.withConnection(connection -> {

            Channel channel =
                    connection.channel();

            channelRef.set(channel);

            String sessionId =
                    channel.attr(
                            SESSION_ID
                    ).get();

            if (sessionId == null) {

                sessionId =
                        sessionManager
                                .nextSessionId();

                channel.attr(
                        SESSION_ID
                ).set(sessionId);

                GatewayLogger.info(
                        log,
                        "TCP connection established "
                                + "awaiting upstream activity "
                                + "session={}",
                        sessionId
                );
            }

            final String sid =
                    sessionId;

            connection.onDispose(() -> {

                sessionManager.remove(
                        sid
                );

                GatewayLogger.info(
                        log,
                        "TCP connection disposed "
                                + "session={}",
                        sid
                );
            });
        });

        return outbound.send(
                inbound.receive()
                        .retain()
                        .flatMap(byteBuf -> {

                            try {

                                Channel channel =
                                        channelRef.get();

                                if (channel == null) {
                                    return Mono.error(
                                            new IllegalStateException(
                                                    "TCP channel unavailable"
                                            )
                                    );
                                }

                                String sessionId =
                                        channel.attr(
                                                SESSION_ID
                                        ).get();

                                String requestPayload =
                                        byteBuf.toString(
                                                StandardCharsets.UTF_8
                                        );

                                return process(
                                        sessionId,
                                        requestPayload
                                )
                                .map(response ->
                                        Unpooled.copiedBuffer(
                                                response,
                                                StandardCharsets.UTF_8
                                        )
                                );

                            } finally {
                                byteBuf.release();
                            }
                        })
        ).then();
    }

    private Mono<String> process(
            String sessionId,
            String requestPayload) {

        final String integrationId =
                gatewayConfig
                        .getUpstream()
                        .getIntegrationId();

        final UpstreamPlugin upstreamPlugin;

        try {

            upstreamPlugin =
                    upstreamPluginRegistry.get(
                            integrationId
                    );

        } catch (Exception e) {

            GatewayLogger.error(
                    log,
                    "Unable to resolve upstream plugin "
                            + "session={} integrationId={}",
                    sessionId,
                    integrationId,
                    e
            );

            return Mono.just(
                    XmlMessages.errorResponse(
                            "96",
                            "SYSTEM_ERROR"
                    )
            );
        }

        final Map<String, String> request;

        try {

            GatewayLogger.info(
                    log,
                    "Upstream parsed request "
                            + "session->{} payload->{}",
                    sessionId,
                    requestPayload
            );

            request =
                    codec.parse(
                            requestPayload
                    );

            GatewayLogger.info(
                    log,
                    "Upstream parsed request "
                            + "session->{} fields->{}",
                    sessionId,
                    request
            );

        } catch (Exception e) {

            GatewayLogger.error(
                    log,
                    "Invalid upstream request "
                            + "session={} integrationId={}",
                    sessionId,
                    integrationId,
                    e
            );

            return Mono.just(
                    XmlMessages.errorResponse(
                            "96",
                            "INVALID_XML"
                    )
            );
        }

        UpstreamRoute route;

        try {

            route =
                    upstreamPlugin.route(
                            requestPayload
                    );

        } catch (Exception e) {

            GatewayLogger.error(
                    log,
                    "Unable to route upstream message "
                            + "session={} integrationId={}",
                    sessionId,
                    integrationId,
                    e
            );

            return Mono.just(
                    XmlMessages.errorResponse(
                            "96",
                            "ROUTING_ERROR"
                    )
            );
        }

        GatewayLogger.debug(
                log,
                "Upstream message detected "
                        + "route={} name={} "
                        + "integrationId={} session={}",
                route.type(),
                route.name(),
                integrationId,
                sessionId
        );

        if (route.isControl()) {

            ClientSession session =
                    getOrCreateSession(
                            sessionId
                    );

            session.touch();

            if ("ECHO".equalsIgnoreCase(
                    route.name())) {

                GatewayLogger.info(
                        log,
                        "RX ECHO "
                                + "integrationId={} "
                                + "session->{} payload->{}",
                        integrationId,
                        sessionId,
                        requestPayload
                );

                String response =
                        XmlMessages.echoResponse();

                GatewayLogger.info(
                        log,
                        "TX ECHO "
                                + "integrationId={} "
                                + "session->{} payload->{}",
                        integrationId,
                        sessionId,
                        response
                );

                return Mono.just(
                        response
                );
            }
        }

        if (route.isTransaction()) {

            ClientSession session =
                    sessionManager.get(
                            sessionId
                    );

            if (session == null) {

                session =
                        getOrCreateSession(
                                sessionId
                        );

                session.touch();
            }

            UpstreamTransactionMetadata metadata =
                    upstreamPlugin.transactionMetadata(
                            requestPayload);

            String transactionId =
                    metadata.transactionId();

            String terminalId =
                    metadata.terminalId();

            TopCoreContext context;

            try {

                context =
                        contextRegistry.create(
                                upstreamPlugin.integrationId(),
                                sessionId,
                                transactionId,
                                requestPayload
                        );

                context.setTerminalId(
                        terminalId == null
                                ? ""
                                : terminalId
                );

                context.setRequestData(
                        request
                );

            } catch (IllegalStateException ex) {

                GatewayLogger.error(
                        log,
                        "Unable to create transaction "
                                + "context session={} "
                                + "transactionId={} "
                                + "integrationId={}",
                        sessionId,
                        transactionId,
                        integrationId,
                        ex
                );

                return Mono.just(
                        XmlMessages.errorResponse(
                                "96",
                                "SYSTEM_BUSY"
                        )
                );
            }

            try {

                CanonicalDocument requestDocument =
                        upstreamPlugin.toCanonical(
                                requestPayload
                        );

                context.setRequestCanonicalDocument(
                        requestDocument
                );

            } catch (Exception ex) {

                GatewayLogger.error(
                        log,
                        "Unable to create upstream request "
                                + "canonical document "
                                + "session={} "
                                + "transactionId={} "
                                + "integrationId={}",
                        sessionId,
                        transactionId,
                        integrationId,
                        ex
                );

                return Mono.just(
                        XmlMessages.errorResponse(
                                "96",
                                "CANONICAL_MAPPING_ERROR"
                        )
                );
            }

            RuntimeMapping inboundMapping =
                    canonicalMappingRegistry
                            .getInbound(
                                    integrationId
                            );

            final String maskedRequestPayload;

            if (inboundMapping != null) {

                maskedRequestPayload =
                        xmlMaskingUtility.mask(
                                requestPayload,
                                inboundMapping
                        );

                context.setMaskedRequestPayload(
                        maskedRequestPayload
                );

            } else {

                maskedRequestPayload =
                        "[INBOUND MASKING CONFIGURATION NOT FOUND]";
            }

            LogContext logContext =
                    new LogContext(
                            String.valueOf(
                                    context.getCorrelationId()
                            )
                    );

            logContext.setSessionId(
                    context.getSessionId()
            );

            logContext.setTerminalId(
                    context.getTerminalId()
            );

            logContext.setClientMsgId(
                    context.getClientMsgId()
            );

            return Mono.defer(() -> {

                GatewayLogger.info(
                        log,
                        "RX {} "
                                + "terminal={} "
                                + "transactionId={} "
                                + "session={} "
                                + "size={}B "
                                + "payload={}",
                        integrationId,
                        context.getTerminalId(),
                        context.getClientMsgId(),
                        sessionId,
                        requestPayload.length(),
                        maskedRequestPayload
                );

                return transactionOrchestrator
                        .process(
                                context
                        );

            }).contextWrite(
                    reactorContext ->
                            reactorContext.put(
                                    LogContextThreadLocalAccessor.KEY,
                                    logContext
                            )
            );
        }

        GatewayLogger.warn(
                log,
                "Unknown upstream message "
                        + "integrationId={} session={}",
                integrationId,
                sessionId
        );

        return Mono.just(
                XmlMessages.errorResponse(
                        "96",
                        "UNKNOWN_MESSAGE"
                )
        );
    }

    private ClientSession getOrCreateSession(
            String sessionId) {

        ClientSession session =
                sessionManager.get(
                        sessionId
                );

        if (session != null) {
            return session;
        }

        session =
                sessionManager.create(
                        sessionId
                );

        GatewayLogger.info(
                log,
                "Application session started "
                        + "session={}",
                sessionId
        );

        return session;
    }
}
