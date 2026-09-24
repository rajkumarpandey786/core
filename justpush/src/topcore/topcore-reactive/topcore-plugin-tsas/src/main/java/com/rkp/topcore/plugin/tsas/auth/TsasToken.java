package com.rkp.topcore.plugin.tsas.auth;

public final class TsasToken {

    private final String accessToken;
    private final long expiresAt;

    public TsasToken(
            String accessToken,
            long expiresAt) {

        if (accessToken == null ||
                accessToken.isBlank()) {

            throw new IllegalArgumentException(
                    "Access token cannot be null or blank");
        }

        this.accessToken =
                accessToken;

        this.expiresAt =
                expiresAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public boolean isValid() {
        return isValid(
                System.currentTimeMillis());
    }

    public boolean isValid(
            long currentTimeMillis) {

        return currentTimeMillis < expiresAt;
    }
}