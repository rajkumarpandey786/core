package com.rkp.mockp.security;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.time.Duration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.reactor.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

@Configuration
public class TlsServerConfig
        implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

    private static final Logger log =
            LoggerFactory.getLogger(TlsServerConfig.class);

    private final SecurityProperties security;

    public TlsServerConfig(
            SecurityProperties security) {

        this.security = security;
    }

    @Override
    public void customize(
            NettyReactiveWebServerFactory factory) {

        String mode = normalizeMode(
                security.getMode());

        switch (mode) {

            case "NONE":

                log.info(
                        "T security mode=NONE - plain HTTP");

                return;

            case "TLS":

                log.info(
                        "T security mode=TLS");

                configureTls(
                        factory,
                        false);

                return;

            case "MTLS":

                log.info(
                        "T security mode=MTLS");

                configureTls(
                        factory,
                        true);

                return;

            default:

                throw new IllegalArgumentException(
                        "Unsupported T security mode="
                                + mode
                                + ". Supported modes: NONE, TLS, MTLS");
        }
    }

    private void configureTls(
            NettyReactiveWebServerFactory factory,
            boolean requireClientAuth) {

        try {

            SecurityProperties.Tls tls =
                    security.getTls();

            KeyManagerFactory keyManagerFactory =
                    createKeyManagerFactory(tls);

            SslContextBuilder builder =
                    SslContextBuilder
                            .forServer(
                                    keyManagerFactory)
                            .sslProvider(
                                    SslProvider.JDK);

            /*
             * TLS:
             *
             * Client certificate is NOT required.
             */
            if (!requireClientAuth) {

                builder.clientAuth(
                        ClientAuth.NONE);
            }

            /*
             * MTLS:
             *
             * Client certificate is mandatory.
             */
            else {

                TrustManagerFactory trustManagerFactory =
                        createTrustManagerFactory(tls);

                builder.trustManager(
                        trustManagerFactory);

                builder.clientAuth(
                        ClientAuth.REQUIRE);
            }

            if (tls.getProtocol() != null &&
                    !tls.getProtocol().isBlank()) {

                builder.protocols(
                        tls.getProtocol());
            }

            SslContext sslContext =
                    builder.build();

            factory.addServerCustomizers(
                    httpServer ->
                            httpServer.secure(
                                    ssl -> ssl
                                            .sslContext(
                                                    sslContext)
                                            .handshakeTimeout(
                                                    Duration.ofMillis(
                                                            tls.getHandshakeTimeoutMs()))));

            log.info(
                    "T SSL initialized mode={} protocol={} " +
                    "keyStore={} trustStore={} clientAuth={}",

                    requireClientAuth
                            ? "MTLS"
                            : "TLS",

                    tls.getProtocol(),

                    tls.getKeyStore().getPath(),

                    requireClientAuth
                            ? tls.getTrustStore().getPath()
                            : "<not-used>",

                    requireClientAuth
                            ? "REQUIRE"
                            : "NONE");

        } catch (Exception ex) {

            throw new IllegalStateException(
                    "Unable to initialize T SSL",
                    ex);
        }
    }

    private KeyManagerFactory createKeyManagerFactory(
            SecurityProperties.Tls tls)
            throws Exception {

        SecurityProperties.KeyStoreConfig cfg =
                tls.getKeyStore();

        validateStore(
                cfg.getType(),
                cfg.getPath(),
                cfg.getPassword(),
                "keyStore");

        KeyStore keyStore =
                KeyStore.getInstance(
                        cfg.getType());

        try (InputStream input =
                     Files.newInputStream(
                             Path.of(cfg.getPath()))) {

            keyStore.load(
                    input,
                    cfg.getPassword()
                            .toCharArray());
        }

        KeyManagerFactory factory =
                KeyManagerFactory.getInstance(
                        KeyManagerFactory
                                .getDefaultAlgorithm());

        factory.init(
                keyStore,
                cfg.getPassword()
                        .toCharArray());

        return factory;
    }

    private TrustManagerFactory createTrustManagerFactory(
            SecurityProperties.Tls tls)
            throws Exception {

        SecurityProperties.TrustStoreConfig cfg =
                tls.getTrustStore();

        validateStore(
                cfg.getType(),
                cfg.getPath(),
                cfg.getPassword(),
                "trustStore");

        KeyStore trustStore =
                KeyStore.getInstance(
                        cfg.getType());

        try (InputStream input =
                     Files.newInputStream(
                             Path.of(cfg.getPath()))) {

            trustStore.load(
                    input,
                    cfg.getPassword()
                            .toCharArray());
        }

        TrustManagerFactory factory =
                TrustManagerFactory.getInstance(
                        TrustManagerFactory
                                .getDefaultAlgorithm());

        factory.init(trustStore);

        return factory;
    }

    private void validateStore(
            String type,
            String path,
            String password,
            String name) {

        if (type == null ||
                type.isBlank()) {

            throw new IllegalArgumentException(
                    name + " type is missing");
        }

        String normalized =
                type.trim().toUpperCase();

        if (!normalized.equals("JKS") &&
                !normalized.equals("PKCS12")) {

            throw new IllegalArgumentException(
                    "Unsupported " +
                    name +
                    " type=" +
                    type +
                    ". Supported types: JKS, PKCS12");
        }

        if (path == null ||
                path.isBlank()) {

            throw new IllegalArgumentException(
                    name + " path is missing");
        }

        if (password == null) {

            throw new IllegalArgumentException(
                    name + " password is missing");
        }
    }

    private String normalizeMode(
            String mode) {

        if (mode == null ||
                mode.isBlank()) {

            return "NONE";
        }

        return mode.trim().toUpperCase();
    }
}