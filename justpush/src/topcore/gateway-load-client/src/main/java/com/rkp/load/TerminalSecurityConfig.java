package com.rkp.load;

public class TerminalSecurityConfig {

    private boolean mtlsEnabled = false;

    private String protocol = "TLSv1.3";

    private long handshakeTimeoutMs = 10000;

    private KeyStoreConfig keyStore = new KeyStoreConfig();

    private TrustStoreConfig trustStore = new TrustStoreConfig();

    public boolean isMtlsEnabled() {
        return mtlsEnabled;
    }

    public void setMtlsEnabled(boolean mtlsEnabled) {
        this.mtlsEnabled = mtlsEnabled;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public long getHandshakeTimeoutMs() {
        return handshakeTimeoutMs;
    }

    public void setHandshakeTimeoutMs(long handshakeTimeoutMs) {
        this.handshakeTimeoutMs = handshakeTimeoutMs;
    }

    public KeyStoreConfig getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(KeyStoreConfig keyStore) {
        this.keyStore = keyStore;
    }

    public TrustStoreConfig getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(TrustStoreConfig trustStore) {
        this.trustStore = trustStore;
    }

    public static class KeyStoreConfig {

        private String type = "PKCS12";

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

    public static class TrustStoreConfig {

        private String type = "PKCS12";

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