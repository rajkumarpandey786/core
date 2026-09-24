package com.rkp.topcore.core.config;

import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.rkp.topcore.core.handler.GatewayHandler;
import com.rkp.topcore.core.observability.GatewayLogger;
import com.rkp.topcore.core.security.SecurityProperties;
import com.rkp.topcore.core.security.TlsContextFactory;

import io.netty.channel.ChannelOption;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;
import jakarta.annotation.PostConstruct;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;


@Component
@ConditionalOnProperty(
        prefix = "gateway.tcp",
        name = "enabled",
        havingValue = "true"
)
public class TcpServerConfig {

    private static final Logger log =
            GatewayLogger.getLogger(
                    TcpServerConfig.class);

    private final GatewayConfig config;

    private final GatewayHandler gatewayHandler;

    private final SecurityProperties securityProperties;

    private final TlsContextFactory tlsContextFactory;

    private DisposableServer server;

    public TcpServerConfig(
            GatewayConfig config,
            GatewayHandler gatewayHandler,
            SecurityProperties securityProperties,
            TlsContextFactory tlsContextFactory) {

        this.config = config;
        this.gatewayHandler = gatewayHandler;
        this.securityProperties = securityProperties;
        this.tlsContextFactory = tlsContextFactory;
    }

    @PostConstruct
    public void start() {

        GatewayConfig.Tcp tcp =
                config.getTcp();

        TcpServer tcpServer =
                TcpServer.create()
                        .host(tcp.getHost())
                        .port(tcp.getPort())
                        .option(
                                ChannelOption.SO_REUSEADDR,
                                true)
                        .childOption(
                                ChannelOption.SO_KEEPALIVE,
                                tcp.isKeepAlive())
                        .childOption(
                                ChannelOption.TCP_NODELAY,
                                tcp.isTcpNoDelay())
                        .childOption(
                                ChannelOption.SO_RCVBUF,
                                tcp.getReceiveBuffer())
                        .childOption(
                                ChannelOption.SO_SNDBUF,
                                tcp.getSendBuffer());

        SecurityProperties.Upstream upstreamSecurity =
                securityProperties.getUpstream();

        String securityMode =
                upstreamSecurity != null
                        ? normalizeMode(
                                upstreamSecurity.getMode())
                        : "NONE";

        GatewayLogger.info(
                log,
                "Upstream security mode={}",
                securityMode);

        switch (securityMode) {

            case "NONE":

                GatewayLogger.info(
                        log,
                        "Upstream TLS disabled - using plain TCP");

                break;

            case "TLS":

                GatewayLogger.info(
                        log,
                        "Upstream TLS enabled protocol={} " +
                        "handshakeTimeout={}ms " +
                        "clientAuth=NONE",
                        upstreamSecurity
                                .getTls()
                                .getProtocol(),
                        upstreamSecurity
                                .getTls()
                                .getHandshakeTimeoutMs());

                SslContext tlsContext =
                        tlsContextFactory
                                .createServerContext(
                                        upstreamSecurity.getTls(),
                                        false);

                tcpServer =
                        tcpServer.secure(
                                ssl -> ssl
                                        .sslContext(tlsContext)
                                        .handshakeTimeout(
                                                java.time.Duration.ofMillis(
                                                        upstreamSecurity
                                                                .getTls()
                                                                .getHandshakeTimeoutMs())));

                break;

            case "MTLS":

                GatewayLogger.info(
                        log,
                        "Upstream mTLS enabled protocol={} " +
                        "handshakeTimeout={}ms " +
                        "clientAuth=REQUIRE",
                        upstreamSecurity
                                .getTls()
                                .getProtocol(),
                        upstreamSecurity
                                .getTls()
                                .getHandshakeTimeoutMs());

                SslContext mtlsContext =
                        tlsContextFactory
                                .createServerContext(
                                        upstreamSecurity.getTls(),
                                        true);

                tcpServer =
                        tcpServer.secure(
                                ssl -> ssl
                                        .sslContext(mtlsContext)
                                        .handshakeTimeout(
                                                java.time.Duration.ofMillis(
                                                        upstreamSecurity
                                                                .getTls()
                                                                .getHandshakeTimeoutMs())));

                break;

            default:

                throw new IllegalArgumentException(
                        "Unsupported upstream security mode: "
                                + securityMode
                                + ". Supported modes: NONE, TLS, MTLS");
        }

        tcpServer =
                tcpServer.doOnConnection(
                        conn -> conn.addHandlerFirst(
                                new LengthFieldBasedFrameDecoder(
                                        tcp.getMaxFrameLength(),
                                        0,
                                        2,
                                        0,
                                        2)));

        tcpServer =
                tcpServer.doOnConnection(
                        conn -> conn.addHandlerLast(
                                new LengthFieldPrepender(2)));

        tcpServer =
                tcpServer.handle(
                        gatewayHandler::handle);

        this.server =
                tcpServer.bindNow();

        GatewayLogger.info(
                log,
                "TCP Gateway started " +
                "host={} port={} " +
                "keepAlive={} tcpNoDelay={} " +
                "receiveBuffer={} sendBuffer={} " +
                "maxFrameLength={} upstreamSecurity={}",
                tcp.getHost(),
                tcp.getPort(),
                tcp.isKeepAlive(),
                tcp.isTcpNoDelay(),
                tcp.getReceiveBuffer(),
                tcp.getSendBuffer(),
                tcp.getMaxFrameLength(),
                securityMode);
    }

    private String normalizeMode(
            String mode) {

        if (mode == null ||
                mode.isBlank()) {

            return "NONE";
        }

        return mode.trim().toUpperCase();
    }

    public DisposableServer getServer() {
        return server;
    }
}