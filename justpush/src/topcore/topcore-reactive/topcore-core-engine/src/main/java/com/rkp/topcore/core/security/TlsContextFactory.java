package com.rkp.topcore.core.security;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.rkp.topcore.core.observability.GatewayLogger;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

@Component
public class TlsContextFactory {

    private static final Logger log =
            GatewayLogger.getLogger(TlsContextFactory.class);

    // ============================================================
    // CLIENT SSL CONTEXT
    // ============================================================

    /**
     * Creates a client-side SSL context.
     *
     * Used later by:
     *
     * Gateway -> T
     * Gateway -> P
     *
     * The Gateway presents its client certificate and validates
     * the downstream server certificate.
     */
    public SslContext createClientContext(
            SecurityProperties.Tls config,
            boolean useClientCertificate) {

        validateClient(config, useClientCertificate);

        try {

            KeyManagerFactory keyManagerFactory =
                    createKeyManagerFactory(config);

            TrustManagerFactory trustManagerFactory =
                    createTrustManagerFactory(config);

            SslContextBuilder builder =
                    SslContextBuilder
                            .forClient()
                            .sslProvider(SslProvider.JDK)
                            .trustManager(trustManagerFactory);

            /*
             * For downstream MTLS:
             *
             * Gateway presents its certificate.
             */
            if (useClientCertificate) {

                builder.keyManager(
                        keyManagerFactory);
            }

            if (config.getProtocol() != null &&
                    !config.getProtocol().isBlank()) {

                builder.protocols(
                        config.getProtocol());
            }

            SslContext sslContext =
                    builder.build();

            GatewayLogger.info(
                    log,
                    "TLS client SSL context initialized " +
                    "keyStore={} trustStore={} " +
                    "protocol={} clientCertificate={}",
                    config.getKeyStore().getPath(),
                    config.getTrustStore().getPath(),
                    config.getProtocol(),
                    useClientCertificate);

            return sslContext;

        } catch (Exception ex) {

            GatewayLogger.error(
                    log,
                    "Unable to initialize TLS client SSL context " +
                    "keyStore={} trustStore={}",
                    config.getKeyStore().getPath(),
                    config.getTrustStore().getPath());

            throw new IllegalStateException(
                    "Unable to initialize TLS client SSL context",
                    ex);
        }
    }

    // ============================================================
    // SERVER SSL CONTEXT
    // ============================================================

    /**
     * Creates a server-side SSL context.
     *
     * Used by:
     *
     * Terminal -> Gateway
     *
     * TLS:
     *     Gateway certificate is presented.
     *     Client certificate is NOT required.
     *
     * MTLS:
     *     Gateway certificate is presented.
     *     Client certificate IS required.
     */
    public SslContext createServerContext(
            SecurityProperties.Tls config,
            boolean requireClientAuth) {

        validateServer(
                config,
                requireClientAuth);

        try {

            KeyManagerFactory keyManagerFactory =
                    createKeyManagerFactory(config);

            SslContextBuilder builder =
                    SslContextBuilder
                            .forServer(
                                    keyManagerFactory)
                            .sslProvider(
                                    SslProvider.JDK);

            /*
             * Trust manager is required only when the
             * Gateway needs to validate client certificates.
             *
             * TLS:
             *     ClientAuth.NONE
             *
             * MTLS:
             *     ClientAuth.REQUIRE
             */
            if (requireClientAuth) {

                TrustManagerFactory trustManagerFactory =
                        createTrustManagerFactory(config);

                builder.trustManager(
                        trustManagerFactory);

                builder.clientAuth(
                        ClientAuth.REQUIRE);

            } else {

                builder.clientAuth(
                        ClientAuth.NONE);
            }

            if (config.getProtocol() != null &&
                    !config.getProtocol().isBlank()) {

                builder.protocols(
                        config.getProtocol());
            }

            SslContext sslContext =
                    builder.build();

            GatewayLogger.info(
                    log,
                    "TLS server SSL context initialized " +
                    "keyStore={} trustStore={} " +
                    "protocol={} clientAuth={}",
                    config.getKeyStore().getPath(),
                    config.getTrustStore().getPath(),
                    config.getProtocol(),
                    requireClientAuth
                            ? "REQUIRE"
                            : "NONE");

            return sslContext;

        } catch (Exception ex) {

            GatewayLogger.error(
                    log,
                    "Unable to initialize TLS server SSL context " +
                    "keyStore={} trustStore={} clientAuth={}",
                    config.getKeyStore().getPath(),
                    config.getTrustStore().getPath(),
                    requireClientAuth);

            throw new IllegalStateException(
                    "Unable to initialize TLS server SSL context",
                    ex);
        }
    }

    // ============================================================
    // KEY MANAGER
    // ============================================================

    private KeyManagerFactory createKeyManagerFactory(
            SecurityProperties.Tls config)
            throws Exception {

        SecurityProperties.KeyStoreConfig keyStoreConfig =
                config.getKeyStore();

        KeyStore keyStore =
                KeyStore.getInstance(
                        keyStoreConfig.getType());

        Path path =
                Path.of(
                        keyStoreConfig.getPath());

        try (InputStream input =
                     Files.newInputStream(path)) {

            keyStore.load(
                    input,
                    keyStoreConfig
                            .getPassword()
                            .toCharArray());
        }

        String algorithm =
                KeyManagerFactory
                        .getDefaultAlgorithm();

        KeyManagerFactory factory =
                KeyManagerFactory.getInstance(
                        algorithm);

        factory.init(
                keyStore,
                keyStoreConfig
                        .getPassword()
                        .toCharArray());

        return factory;
    }

    // ============================================================
    // TRUST MANAGER
    // ============================================================

    private TrustManagerFactory createTrustManagerFactory(
            SecurityProperties.Tls config)
            throws Exception {

        SecurityProperties.TrustStoreConfig trustStoreConfig =
                config.getTrustStore();

        KeyStore trustStore =
                KeyStore.getInstance(
                        trustStoreConfig.getType());

        Path path =
                Path.of(
                        trustStoreConfig.getPath());

        try (InputStream input =
                     Files.newInputStream(path)) {

            trustStore.load(
                    input,
                    trustStoreConfig
                            .getPassword()
                            .toCharArray());
        }

        String algorithm =
                TrustManagerFactory
                        .getDefaultAlgorithm();

        TrustManagerFactory factory =
                TrustManagerFactory.getInstance(
                        algorithm);

        factory.init(trustStore);

        return factory;
    }

    // ============================================================
    // STORE TYPE VALIDATION
    // ============================================================

    private void validateStoreType(
            String type,
            String storeName) {

        if (type == null ||
                type.isBlank()) {

            throw new IllegalArgumentException(
                    "TLS " +
                    storeName +
                    " type is missing");
        }

        String normalized =
                type.trim().toUpperCase();

        if (!normalized.equals("JKS") &&
                !normalized.equals("PKCS12")) {

            throw new IllegalArgumentException(
                    "Unsupported TLS " +
                    storeName +
                    " type: " +
                    type +
                    ". Supported types are JKS and PKCS12");
        }
    }

    // ============================================================
    // COMMON KEYSTORE VALIDATION
    // ============================================================

    private void validateKeyStore(
            SecurityProperties.Tls config) {

        if (config == null) {

            throw new IllegalArgumentException(
                    "TLS configuration must not be null");
        }

        if (config.getKeyStore() == null) {

            throw new IllegalArgumentException(
                    "TLS keyStore configuration is missing");
        }

        validateStoreType(
                config.getKeyStore().getType(),
                "keyStore");

        if (config.getKeyStore().getPath() == null ||
                config.getKeyStore().getPath().isBlank()) {

            throw new IllegalArgumentException(
                    "TLS keyStore path is missing");
        }

        if (config.getKeyStore().getPassword() == null) {

            throw new IllegalArgumentException(
                    "TLS keyStore password is missing");
        }
    }

    // ============================================================
    // SERVER VALIDATION
    // ============================================================

    private void validateServer(
            SecurityProperties.Tls config,
            boolean requireClientAuth) {

        /*
         * Server always needs its own certificate.
         */
        validateKeyStore(config);

        /*
         * TrustStore is required only for MTLS.
         */
        if (requireClientAuth) {

            validateTrustStore(config);
        }
    }

    // ============================================================
    // CLIENT VALIDATION
    // ============================================================

    private void validateClient(
            SecurityProperties.Tls config,
            boolean useClientCertificate) {

        /*
         * Gateway must validate the downstream server.
         */
        validateTrustStore(config);

        /*
         * Gateway needs its own certificate only when
         * downstream requires mTLS.
         */
        if (useClientCertificate) {

            validateKeyStore(config);
        }
    }

    // ============================================================
    // TRUSTSTORE VALIDATION
    // ============================================================

    private void validateTrustStore(
            SecurityProperties.Tls config) {

        if (config.getTrustStore() == null) {

            throw new IllegalArgumentException(
                    "TLS trustStore configuration is missing");
        }

        validateStoreType(
                config.getTrustStore().getType(),
                "trustStore");

        if (config.getTrustStore().getPath() == null ||
                config.getTrustStore().getPath().isBlank()) {

            throw new IllegalArgumentException(
                    "TLS trustStore path is missing");
        }

        if (config.getTrustStore().getPassword() == null) {

            throw new IllegalArgumentException(
                    "TLS trustStore password is missing");
        }
    }
}