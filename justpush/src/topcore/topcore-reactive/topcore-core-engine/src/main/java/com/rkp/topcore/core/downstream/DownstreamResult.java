package com.rkp.topcore.core.downstream;

public record DownstreamResult(

        String system,

        String responseCode,

        String responseMessage,

        long latencyMs,

        boolean timeout,

        boolean success) {

    public static DownstreamResult success(
            String system,
            String responseCode,
            String responseMessage,
            long latencyMs) {

        return new DownstreamResult(
                system,
                responseCode,
                responseMessage,
                latencyMs,
                false,
                "00".equals(responseCode));
    }

    public static DownstreamResult timeout(
            String system,
            long latencyMs) {

        return new DownstreamResult(
                system,
                "TIMEOUT",
                "TIMEOUT",
                latencyMs,
                true,
                false);
    }

    public static DownstreamResult unavailable(
            String system,
            long latencyMs) {

        return new DownstreamResult(
                system,
                "UNAVAILABLE",
                "CONNECTION_ERROR",
                latencyMs,
                true,
                false);
    }
}
