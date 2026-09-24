package com.rkp.topcore.core.security;

public class MtlsProperties {

    private boolean enabled = false;

    private String protocol = "TLSv1.3";

    private long handshakeTimeoutMs = 10000;

    private KeyStoreProperties keyStore =
            new KeyStoreProperties();

    private KeyStoreProperties trustStore =
            new KeyStoreProperties();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public void setHandshakeTimeoutMs(
            long handshakeTimeoutMs) {

        this.handshakeTimeoutMs =
                handshakeTimeoutMs;
    }

    public KeyStoreProperties getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(
            KeyStoreProperties keyStore) {

        this.keyStore = keyStore;
    }

    public KeyStoreProperties getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(
            KeyStoreProperties trustStore) {

        this.trustStore = trustStore;
    }
}