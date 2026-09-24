package com.rkp.topcore.core.orchestrator;

public record AggregatedResult(

        boolean success,

        String responseCode,

        String message,

        String action,

        String hostCode,

        String settlement) {

    public static AggregatedResult approve() {

        return new AggregatedResult(
                true,
                "00",
                "APPROVED",
                "FORWARD",
                "H000",
                "POST");
    }

    public static AggregatedResult decline(
            String responseCode,
            String message) {

        return new AggregatedResult(
                false,
                responseCode,
                message,
                "REJECT",
                "H" + responseCode,
                "NONE");
    }

    public static AggregatedResult timeout() {

        return new AggregatedResult(
                false,
                "91",
                "TIMEOUT",
                "RETRY",
                "H091",
                "NONE");
    }

    public static AggregatedResult systemError() {

        return new AggregatedResult(
                false,
                "96",
                "SYSTEM_ERROR",
                "REJECT",
                "H096",
                "NONE");
    }
}
