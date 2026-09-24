package com.rkp.topcore.core.database.model;

import java.time.LocalDateTime;

public class TransactionRecord {

    private Long id;

    private String correlationId;

    private String messageId;

    private String sessionId;

    private String terminalId;

    private String sourceSystem;

    private String status;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private Long latencyMs;

    private LocalDateTime createdAt;


    public Long getId() {

        return id;
    }

    public void setId(
            Long id) {

        this.id = id;
    }


    public String getCorrelationId() {

        return correlationId;
    }

    public void setCorrelationId(
            String correlationId) {

        this.correlationId = correlationId;
    }


    public String getMessageId() {

        return messageId;
    }

    public void setMessageId(
            String messageId) {

        this.messageId = messageId;
    }


    public String getSessionId() {

        return sessionId;
    }

    public void setSessionId(
            String sessionId) {

        this.sessionId = sessionId;
    }


    public String getTerminalId() {

        return terminalId;
    }

    public void setTerminalId(
            String terminalId) {

        this.terminalId = terminalId;
    }


    public String getSourceSystem() {

        return sourceSystem;
    }

    public void setSourceSystem(
            String sourceSystem) {

        this.sourceSystem = sourceSystem;
    }


    public String getStatus() {

        return status;
    }

    public void setStatus(
            String status) {

        this.status = status;
    }


    public LocalDateTime getStartedAt() {

        return startedAt;
    }

    public void setStartedAt(
            LocalDateTime startedAt) {

        this.startedAt = startedAt;
    }


    public LocalDateTime getCompletedAt() {

        return completedAt;
    }

    public void setCompletedAt(
            LocalDateTime completedAt) {

        this.completedAt = completedAt;
    }


    public Long getLatencyMs() {

        return latencyMs;
    }

    public void setLatencyMs(
            Long latencyMs) {

        this.latencyMs = latencyMs;
    }


    public LocalDateTime getCreatedAt() {

        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }
}