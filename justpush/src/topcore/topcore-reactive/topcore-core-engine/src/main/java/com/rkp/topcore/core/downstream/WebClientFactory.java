package com.rkp.topcore.core.downstream;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.rkp.topcore.core.config.GatewayConfig;
import com.rkp.topcore.core.observability.GatewayLogger;
import com.rkp.topcore.core.security.SecurityProperties;
import com.rkp.topcore.core.security.TlsContextFactory;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Component
public class WebClientFactory {

    private static final Logger log =
            GatewayLogger.getLogger(WebClientFactory.class);

    private final GatewayConfig gatewayConfig;

    private final SecurityProperties securityProperties;

    private final TlsContextFactory tlsContextFactory;

    /*
     * One WebClient per downstream system.
     *
     * T -> T WebClient -> T connection pool
     * P -> P WebClient -> P connection pool
     *
     * WebClient is thread-safe and reusable.
     */
    private final Map<String, WebClient> clients =
            new ConcurrentHashMap<>();

    public WebClientFactory(
            GatewayConfig gatewayConfig,
            SecurityProperties securityProperties,
            TlsContextFactory tlsContextFactory) {

        this.gatewayConfig = gatewayConfig;
        this.securityProperties = securityProperties;
        this.tlsContextFactory = tlsContextFactory;
    }

    /**
     * Returns an existing WebClient or creates it once.
     */
    public WebClient create(String system) {

        return clients.computeIfAbsent(
                system,
                this::createWebClient);
    }

    /**
     * Creates the WebClient and dedicated connection pool
     * for one downstream system.
     */
    private WebClient createWebClient(String system) {

        GatewayConfig.Downstream.SystemConfig cfg =
                gatewayConfig.getDownstream()
                        .getSystems()
                        .get(system);

        if (cfg == null) {

            throw new IllegalStateException(
                    "No downstream configuration found for system: "
                            + system);
        }

        String poolName =
                "gateway-" +
                system.toLowerCase() +
                "-pool";

        // =========================================================
        // CONNECTION POOL
        // =========================================================

        ConnectionProvider provider =
                ConnectionProvider.builder(poolName)

                        .maxConnections(
                                cfg.getMaxConnections())

                        .pendingAcquireMaxCount(
                                cfg.getPendingAcquireMaxCount())

                        .pendingAcquireTimeout(
                                Duration.ofMillis(
                                        cfg.getPendingAcquireTimeoutMs()))

                        .maxIdleTime(
                                Duration.ofMillis(
                                        cfg.getMaxIdleTimeMs()))

                        .maxLifeTime(
                                Duration.ofMillis(
                                        cfg.getMaxLifeTimeMs()))

                        .build();

        // =========================================================
        // BASE HTTP CLIENT
        // =========================================================

        HttpClient httpClient =
                HttpClient.create(provider)

                        .option(
                                ChannelOption.CONNECT_TIMEOUT_MILLIS,
                                cfg.getConnectTimeoutMs())

                        .responseTimeout(
                                Duration.ofMillis(
                                        cfg.getResponseTimeoutMs()))

                        .keepAlive(
                                cfg.isKeepAlive());

        // =========================================================
        // DOWNSTREAM SECURITY
        // =========================================================

        SecurityProperties.SystemSecurity systemSecurity =
                securityProperties
                        .getDownstream()
                        .getSystems()
                        .get(system);

        String securityMode =
                getSecurityMode(systemSecurity);

        GatewayLogger.info(
                log,
                "Downstream security mode system={} mode={}",
                system,
                securityMode);

        switch (securityMode) {

            // -----------------------------------------------------
            // NONE
            // -----------------------------------------------------

            case "NONE":

                GatewayLogger.info(
                        log,
                        "Downstream TLS disabled system={} " +
                        "- using plain HTTP",
                        system);

                break;

            // -----------------------------------------------------
            // TLS
            // -----------------------------------------------------

            case "TLS":

                httpClient =
                        configureTls(
                                httpClient,
                                systemSecurity,
                                system,
                                false);

                break;

            // -----------------------------------------------------
            // MTLS
            // -----------------------------------------------------

            case "MTLS":

                httpClient =
                        configureTls(
                                httpClient,
                                systemSecurity,
                                system,
                                true);

                break;

            // -----------------------------------------------------
            // INVALID
            // -----------------------------------------------------

            default:

                throw new IllegalArgumentException(
                        "Unsupported downstream security mode: "
                                + securityMode
                                + " for system="
                                + system
                                + ". Supported modes: NONE, TLS, MTLS");
        }

        // =========================================================
        // OBSERVABILITY
        // =========================================================

        GatewayLogger.info(
                log,
                "WebClient initialized system={} " +
                "pool={} " +
                "maxConnections={} " +
                "pendingAcquireMax={} " +
                "pendingAcquireTimeout={}ms " +
                "connectTimeout={}ms " +
                "responseTimeout={}ms " +
                "idleTime={}ms " +
                "lifeTime={}ms " +
                "keepAlive={} " +
                "securityMode={}",

                system,

                poolName,

                cfg.getMaxConnections(),

                cfg.getPendingAcquireMaxCount(),

                cfg.getPendingAcquireTimeoutMs(),

                cfg.getConnectTimeoutMs(),

                cfg.getResponseTimeoutMs(),

                cfg.getMaxIdleTimeMs(),

                cfg.getMaxLifeTimeMs(),

                cfg.isKeepAlive(),

                securityMode);

        // =========================================================
        // WEBCLIENT
        // =========================================================

        return WebClient.builder()

                .clientConnector(
                        new ReactorClientHttpConnector(
                                httpClient))

                .build();
    }

    // =============================================================
    // CONFIGURE TLS / MTLS
    // =============================================================

    private HttpClient configureTls(
            HttpClient httpClient,
            SecurityProperties.SystemSecurity systemSecurity,
            String system,
            boolean mtls) {

        if (systemSecurity == null) {

            throw new IllegalStateException(
                    "Security configuration is missing for "
                            + "downstream system="
                            + system);
        }

        SecurityProperties.Tls tls =
                systemSecurity.getTls();

        if (tls == null) {

            throw new IllegalStateException(
                    "TLS configuration is missing for "
                            + "downstream system="
                            + system);
        }

        /*
         * TLS:
         *
         * Gateway validates downstream server certificate.
         *
         * Gateway does NOT present a client certificate.
         *
         * MTLS:
         *
         * Gateway validates downstream server certificate.
         *
         * Gateway ALSO presents its client certificate.
         */
        SslContext sslContext =
                tlsContextFactory.createClientContext(
                        tls,
                        mtls);

        /*
         * Apply SSL configuration to Reactor Netty.
         */
        httpClient =
                httpClient.secure(
                        ssl -> ssl
                                .sslContext(sslContext)
                                .handshakeTimeout(
                                        Duration.ofMillis(
                                                tls.getHandshakeTimeoutMs())));

        if (mtls) {

            GatewayLogger.info(
                    log,
                    "Downstream mTLS enabled " +
                    "system={} protocol={} " +
                    "handshakeTimeout={}ms " +
                    "keyStore={} " +
                    "trustStore={}",

                    system,

                    tls.getProtocol(),

                    tls.getHandshakeTimeoutMs(),

                    tls.getKeyStore().getPath(),

                    tls.getTrustStore().getPath());

        } else {

            GatewayLogger.info(
                    log,
                    "Downstream TLS enabled " +
                    "system={} protocol={} " +
                    "handshakeTimeout={}ms " +
                    "trustStore={}",

                    system,

                    tls.getProtocol(),

                    tls.getHandshakeTimeoutMs(),

                    tls.getTrustStore().getPath());
        }

        return httpClient;
    }

    // =============================================================
    // SECURITY MODE
    // =============================================================

    private String getSecurityMode(
            SecurityProperties.SystemSecurity systemSecurity) {

        if (systemSecurity == null ||
                systemSecurity.getMode() == null ||
                systemSecurity.getMode().isBlank()) {

            return "NONE";
        }

        return systemSecurity
                .getMode()
                .trim()
                .toUpperCase();
    }
}