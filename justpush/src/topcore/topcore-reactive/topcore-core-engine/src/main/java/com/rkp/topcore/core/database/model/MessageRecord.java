package com.rkp.topcore.core.database.model;

import java.time.LocalDateTime;

public class MessageRecord {

    private Long id;

    private Long transactionId;

    private String systemName;

    private String direction;

    private String messageType;

    private String payload;

    private String maskedPayload;

    private String status;

    private Long latencyMs;

    private LocalDateTime createdAt;


    public Long getId() {

        return id;
    }

    public void setId(
            Long id) {

        this.id = id;
    }


    public Long getTransactionId() {

        return transactionId;
    }

    public void setTransactionId(
            Long transactionId) {

        this.transactionId = transactionId;
    }


    public String getSystemName() {

        return systemName;
    }

    public void setSystemName(
            String systemName) {

        this.systemName = systemName;
    }


    public String getDirection() {

        return direction;
    }

    public void setDirection(
            String direction) {

        this.direction = direction;
    }


    public String getMessageType() {

        return messageType;
    }

    public void setMessageType(
            String messageType) {

        this.messageType = messageType;
    }


    public String getPayload() {

        return payload;
    }

    public void setPayload(
            String payload) {

        this.payload = payload;
    }


    public String getMaskedPayload() {

        return maskedPayload;
    }

    public void setMaskedPayload(
            String maskedPayload) {

        this.maskedPayload = maskedPayload;
    }


    public String getStatus() {

        return status;
    }

    public void setStatus(
            String status) {

        this.status = status;
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