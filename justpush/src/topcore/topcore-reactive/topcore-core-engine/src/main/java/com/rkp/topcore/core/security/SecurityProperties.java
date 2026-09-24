package com.rkp.topcore.core.security;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway.security")
public class SecurityProperties {

    private final Upstream upstream = new Upstream();

    private final Downstream downstream = new Downstream();

    public Upstream getUpstream() {
        return upstream;
    }

    public Downstream getDownstream() {
        return downstream;
    }

    // ============================================================
    // UPSTREAM
    // ============================================================

    public static class Upstream {

        /*
         * Supported modes:
         *
         * NONE  = Plain TCP
         * TLS   = TLS without client certificate
         * MTLS  = Mutual TLS with mandatory client certificate
         */
        private String mode = "NONE";

        private Tls tls = new Tls();

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public Tls getTls() {
            return tls;
        }

        public void setTls(Tls tls) {
            this.tls = tls;
        }
    }

    // ============================================================
    // DOWNSTREAM
    // ============================================================

    public static class Downstream {

        private Map<String, SystemSecurity> systems =
                new LinkedHashMap<>();

        public Map<String, SystemSecurity> getSystems() {
            return systems;
        }

        public void setSystems(
                Map<String, SystemSecurity> systems) {

            this.systems = systems;
        }
    }

    public static class SystemSecurity {

        /*
         * This will later support:
         *
         * NONE
         * TLS
         * MTLS
         *
         * independently for T and P.
         */
        private String mode = "NONE";

        private Tls tls = new Tls();

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public Tls getTls() {
            return tls;
        }

        public void setTls(Tls tls) {
            this.tls = tls;
        }
    }

    // ============================================================
    // COMMON TLS CONFIGURATION
    // ============================================================

    public static class Tls {

        private String protocol = "TLSv1.3";

        private long handshakeTimeoutMs = 10000;

        /*
         * Required for TLS and MTLS because the local side
         * must present its certificate.
         *
         * Upstream:
         *     Gateway server certificate
         *
         * Downstream:
         *     Gateway client certificate
         */
        private KeyStoreConfig keyStore =
                new KeyStoreConfig();

        /*
         * Required for MTLS.
         *
         * For TLS-only server mode, the trust store is not
         * used for client certificate authentication.
         */
        private TrustStoreConfig trustStore =
                new TrustStoreConfig();

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public long getHandshakeTimeoutMs() {
            return handshakeTimeoutMs;
        }

        public void setHandshakeTimeoutMs(
                long handshakeTimeoutMs) {

            this.handshakeTimeoutMs =
                    handshakeTimeoutMs;
        }

        public KeyStoreConfig getKeyStore() {
            return keyStore;
        }

        public void setKeyStore(
                KeyStoreConfig keyStore) {

            this.keyStore = keyStore;
        }

        public TrustStoreConfig getTrustStore() {
            return trustStore;
        }

        public void setTrustStore(
                TrustStoreConfig trustStore) {

            this.trustStore = trustStore;
        }
    }

    // ============================================================
    // KEYSTORE
    // ============================================================

    public static class KeyStoreConfig {

        /*
         * No default.
         *
         * The format must be explicitly configured in YAML.
         *
         * Supported:
         *     JKS
         *     PKCS12
         */
        private String type;

        private String path;

        private String password;

        private String keyAlias;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getKeyAlias() {
            return keyAlias;
        }

        public void setKeyAlias(String keyAlias) {
            this.keyAlias = keyAlias;
        }
    }

    // ============================================================
    // TRUSTSTORE
    // ============================================================

    public static class TrustStoreConfig {

        /*
         * No default.
         *
         * Supported:
         *     JKS
         *     PKCS12
         */
        private String type;

        private String path;

        private String password;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}