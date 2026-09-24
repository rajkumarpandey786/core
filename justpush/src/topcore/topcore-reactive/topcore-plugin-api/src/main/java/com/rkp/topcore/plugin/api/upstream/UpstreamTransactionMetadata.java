package com.rkp.topcore.plugin.api.upstream;

public final class UpstreamTransactionMetadata {

    private final String transactionId;
    private final String terminalId;

    public UpstreamTransactionMetadata(
            String transactionId,
            String terminalId) {

        this.transactionId = transactionId;
        this.terminalId = terminalId;
    }

    public String transactionId() {
        return transactionId;
    }

    public String terminalId() {
        return terminalId;
    }

    public boolean hasTransactionId() {
        return transactionId != null &&
                !transactionId.isBlank();
    }

    public boolean hasTerminalId() {
        return terminalId != null &&
                !terminalId.isBlank();
    }
}