package com.rkp.topcore.core.observability;

import java.util.concurrent.atomic.AtomicLong;

public class LogContext {

    private final String correlationId;

    private String sessionId;

    private String terminalId;

    private String clientMsgId;

    private final long startTime;

    private final AtomicLong sequence = new AtomicLong();

    public LogContext(String correlationId) {

        this.correlationId = correlationId;

        this.startTime = System.currentTimeMillis();
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getClientMsgId() {
        return clientMsgId;
    }

    public void setClientMsgId(String clientMsgId) {
        this.clientMsgId = clientMsgId;
    }

    public long getStartTime() {
        return startTime;
    }

    public long nextSequence() {
        return sequence.incrementAndGet();
    }
}