package com.rkp.load;

import java.util.ArrayList;
import java.util.List;

public class LoadTestClient {

    public static void main(String[] args) {

        try {

            MetricsCollector metrics =
                    new MetricsCollector();

            List<TerminalClient> clients =
                    new ArrayList<>();

            String terminalId =
                    String.format("TERM%03d", 1);

            /*
             * ---------------------------------------------------------
             * UPSTREAM CONNECTION SECURITY
             * ---------------------------------------------------------
             *
             * false:
             *     Plain TCP
             *
             * true:
             *     TLS/mTLS TCP
             *
             * The same client can therefore test both modes.
             */
            boolean mtlsEnabled =
                    Boolean.parseBoolean(
                            System.getProperty(
                                    "client.mtls.enabled",
                                    "true"));

            /*
             * ---------------------------------------------------------
             * SECURITY CONFIGURATION
             * ---------------------------------------------------------
             *
             * Passwords and certificate paths are supplied through
             * environment variables.
             *
             * Nothing sensitive is hard-coded in the Java source.
             */
            String keyStorePath =
                    System.getenv("CLIENT_KEYSTORE_PATH");

            String keyStorePassword =
                    System.getenv("CLIENT_KEYSTORE_PASSWORD");

            String trustStorePath =
                    System.getenv("CLIENT_TRUSTSTORE_PATH");

            String trustStorePassword =
                    System.getenv("CLIENT_TRUSTSTORE_PASSWORD");

            String keyAlias =
                    System.getenv("CLIENT_KEY_ALIAS");

            String protocol =
                    System.getProperty(
                            "client.mtls.protocol",
                            "TLSv1.3");

            long handshakeTimeoutMs =
                    Long.parseLong(
                            System.getProperty(
                                    "client.mtls.handshakeTimeoutMs",
                                    "10000"));

            /*
             * ---------------------------------------------------------
             * DEBUG CONFIGURATION OUTPUT
             * ---------------------------------------------------------
             *
             * Do NOT print passwords.
             */
            System.out.println(
                    "Client Security Configuration:");
            System.out.println(
                    "  mTLS Enabled       = " + mtlsEnabled);
            System.out.println(
                    "  KeyStore Path      = " + keyStorePath);
            System.out.println(
                    "  TrustStore Path    = " + trustStorePath);
            System.out.println(
                    "  Key Alias          = " + keyAlias);
            System.out.println(
                    "  Protocol           = " + protocol);
            System.out.println(
                    "  Handshake Timeout  = "
                            + handshakeTimeoutMs + "ms");

            /*
             * ---------------------------------------------------------
             * VALIDATION
             * ---------------------------------------------------------
             *
             * Certificate configuration is required only when
             * mTLS is enabled.
             */
            if (mtlsEnabled) {

                validateRequired(
                        "CLIENT_KEYSTORE_PATH",
                        keyStorePath);

                validateRequired(
                        "CLIENT_KEYSTORE_PASSWORD",
                        keyStorePassword);

                validateRequired(
                        "CLIENT_TRUSTSTORE_PATH",
                        trustStorePath);

                validateRequired(
                        "CLIENT_TRUSTSTORE_PASSWORD",
                        trustStorePassword);

                validateRequired(
                        "CLIENT_KEY_ALIAS",
                        keyAlias);
            }

            /*
             * ---------------------------------------------------------
             * CREATE TERMINAL SECURITY CONFIGURATION
             * ---------------------------------------------------------
             */
            TerminalSecurityConfig securityConfig =
                    new TerminalSecurityConfig();

            securityConfig.setMtlsEnabled(
                    mtlsEnabled);

            securityConfig.setProtocol(
                    protocol);

            securityConfig.setHandshakeTimeoutMs(
                    handshakeTimeoutMs);

            /*
             * KeyStore configuration
             */
            TerminalSecurityConfig.KeyStoreConfig keyStore =
                    securityConfig.getKeyStore();

            keyStore.setType("JKS");//PKCS12 || JKS
            keyStore.setPath(keyStorePath);
            keyStore.setPassword(keyStorePassword);
            keyStore.setKeyAlias(keyAlias);

            /*
             * TrustStore configuration
             */
            TerminalSecurityConfig.TrustStoreConfig trustStore =
                    securityConfig.getTrustStore();

            keyStore.setType("JKS");//PKCS12 || JKS
            trustStore.setPath(trustStorePath);
            trustStore.setPassword(trustStorePassword);

            /*
             * ---------------------------------------------------------
             * CREATE TERMINAL
             * ---------------------------------------------------------
             */
            for (int i = 1; i <= 1; i++) {

                String termlId = String.format("TERM%03d", i);

                TerminalClient client = new TerminalClient(
                        "localhost",
                        9000,
                        termlId,
                        500,
                        metrics, securityConfig);

                clients.add(client);
            }

            /*
             * ---------------------------------------------------------
             * START TERMINAL CLIENTS
             * ---------------------------------------------------------
             */
            clients.forEach(
                    TerminalClient::start);

            /*
             * ---------------------------------------------------------
             * METRICS LOOP
             * ---------------------------------------------------------
             */
            while (true) {

                Thread.sleep(1000);

                metrics.printAndReset();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static void validateRequired(
            String name,
            String value) {

        if (value == null ||
                value.isBlank()) {

            throw new IllegalArgumentException(
                    name +
                    " must be configured when mTLS is enabled");
        }
    }
}