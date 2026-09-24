package com.rkp.topcore.hazelcast.core;

import java.time.Instant;

public record AccessToken(
        String value,
        Instant expiresAt
) {

    /**
     * Determines whether the token is usable.
     *
     * @param safetyWindowSeconds
     *        number of seconds before actual expiry
     *        during which the token is considered expired
     */
    public boolean isValid(long safetyWindowSeconds) {

        if (value == null || value.isBlank()) {
            return false;
        }

        Instant effectiveExpiry =
                expiresAt.minusSeconds(safetyWindowSeconds);

        return Instant.now().isBefore(effectiveExpiry);
    }
}
