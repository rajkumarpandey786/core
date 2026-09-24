package com.rkp.topcore.core.context;

public class DownstreamState {

    private volatile String requestPayload;

    private volatile String responsePayload;

    private volatile long startNanos;

    private volatile long endNanos;

    public void markStart() {
        startNanos = System.nanoTime();
    }

    public void markEnd() {
        endNanos = System.nanoTime();
    }

    public long latencyMicros() {

        if (startNanos == 0 || endNanos == 0) {
            return -1;
        }

        return (endNanos - startNanos) / 1000;
    }

    public long latencyMillis() {

        if (startNanos == 0 || endNanos == 0) {
            return -1;
        }

        return (endNanos - startNanos) / 1_000_000;
    }

    public String getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(String responsePayload) {
        this.responsePayload = responsePayload;
    }

    public long getStartNanos() {
        return startNanos;
    }

    public long getEndNanos() {
        return endNanos;
    }
}