package com.rkp.topcore.core.context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.document.CanonicalDocumentType;

import reactor.core.publisher.Sinks;

public class TopCoreContext {

    private final long correlationId;

    private final String integrationId;

    private final String sessionId;

    private final String clientMsgId;

    private final String requestPayload;

    private final long gatewayStartNanos;

    private volatile long gatewayEndNanos;

    private volatile String terminalId;

    private Map<String, String> requestData =
            new HashMap<>();

    private Long databaseTransactionId;

    private volatile String maskedRequestPayload;

    private volatile CanonicalDocument
            requestCanonicalDocument;

    private final ConcurrentHashMap<
            String,
            CanonicalDocument>
            downstreamRequestCanonicalDocuments =
                    new ConcurrentHashMap<>();

    private final ConcurrentHashMap<
            String,
            CanonicalDocument>
            downstreamResponseCanonicalDocuments =
                    new ConcurrentHashMap<>();

    private volatile CanonicalDocument
            commonResponseCanonicalDocument;

    private final ConcurrentHashMap<
            String,
            DownstreamState>
            downstreamStates =
                    new ConcurrentHashMap<>();

    private final Sinks.One<String> completionSink =
            Sinks.one();

    private final AtomicBoolean completed =
            new AtomicBoolean(false);

    public TopCoreContext(
            long correlationId,
            String integrationId,
            String sessionId,
            String clientMsgId,
            String requestPayload) {

        if (integrationId == null ||
                integrationId.isBlank()) {

            throw new IllegalArgumentException(
                    "integrationId cannot be null or blank");
        }

        this.correlationId = correlationId;

        this.integrationId =
                integrationId.trim();

        this.sessionId = sessionId;

        this.clientMsgId = clientMsgId;

        this.requestPayload = requestPayload;

        this.gatewayStartNanos =
                System.nanoTime();
    }

    
    public Long getDatabaseTransactionId() {
		return databaseTransactionId;
	}


	public void setDatabaseTransactionId(Long databaseTransactionId) {
		this.databaseTransactionId = databaseTransactionId;
	}


	public long getCorrelationId() {
        return correlationId;
    }

    public String getIntegrationId() {
        return integrationId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getClientMsgId() {
        return clientMsgId;
    }

    public String getRequestPayload() {
        return requestPayload;
    }

    public long getGatewayStartNanos() {
        return gatewayStartNanos;
    }

    public long getGatewayEndNanos() {
        return gatewayEndNanos;
    }

    public void markGatewayCompleted() {
        this.gatewayEndNanos =
                System.nanoTime();

        this.completed.set(true);
    }

    public boolean isCompleted() {
        return completed.get();
    }

    public String getMaskedRequestPayload() {
        return maskedRequestPayload;
    }

    public void setMaskedRequestPayload(
            String maskedRequestPayload) {

        this.maskedRequestPayload =
                maskedRequestPayload;
    }

    public long gatewayLatencyMicros() {

        long end =
                gatewayEndNanos == 0
                        ? System.nanoTime()
                        : gatewayEndNanos;

        return (end - gatewayStartNanos)
                / 1000;
    }

    public long gatewayLatencyMillis() {

        long end =
                gatewayEndNanos == 0
                        ? System.nanoTime()
                        : gatewayEndNanos;

        return (end - gatewayStartNanos)
                / 1_000_000;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(
            String terminalId) {

        this.terminalId =
                terminalId;
    }

    public Map<String, String>
            getRequestData() {

        return requestData;
    }

    public void setRequestData(
            Map<String, String> requestData) {

        this.requestData =
                requestData;
    }

    public CanonicalDocument
            getRequestCanonicalDocument() {

        return requestCanonicalDocument;
    }

    public void setRequestCanonicalDocument(
            CanonicalDocument document) {

        validateDocument(
                document,
                CanonicalDocumentType.REQUEST,
                "Request");

        this.requestCanonicalDocument =
                document;
    }

    public void setDownstreamRequestCanonicalDocument(
            String systemName,
            CanonicalDocument document) {

        validateSystemName(systemName);

        validateDocument(
                document,
                CanonicalDocumentType.REQUEST,
                "Downstream request");

        downstreamRequestCanonicalDocuments.put(
                systemName.trim(),
                document);
    }

    public CanonicalDocument
            getDownstreamRequestCanonicalDocument(
                    String systemName) {

        if (systemName == null ||
                systemName.isBlank()) {

            return null;
        }

        return downstreamRequestCanonicalDocuments.get(
                systemName.trim());
    }

    public boolean hasDownstreamRequestCanonicalDocument(
            String systemName) {

        return getDownstreamRequestCanonicalDocument(
                systemName) != null;
    }

    public void removeDownstreamRequestCanonicalDocument(
            String systemName) {

        if (systemName == null ||
                systemName.isBlank()) {

            return;
        }

        downstreamRequestCanonicalDocuments.remove(
                systemName.trim());
    }

    public Map<String, CanonicalDocument>
            getDownstreamRequestCanonicalDocuments() {

        return Map.copyOf(
                downstreamRequestCanonicalDocuments);
    }

    public void setDownstreamResponseCanonicalDocument(
            String systemName,
            CanonicalDocument document) {

        validateSystemName(systemName);

        validateDocument(
                document,
                CanonicalDocumentType.RESPONSE,
                "Downstream response");

        downstreamResponseCanonicalDocuments.put(
                systemName.trim(),
                document);
    }

    public CanonicalDocument
            getDownstreamResponseCanonicalDocument(
                    String systemName) {

        if (systemName == null ||
                systemName.isBlank()) {

            return null;
        }

        return downstreamResponseCanonicalDocuments.get(
                systemName.trim());
    }

    public boolean hasDownstreamResponseCanonicalDocument(
            String systemName) {

        return getDownstreamResponseCanonicalDocument(
                systemName) != null;
    }

    public void removeDownstreamResponseCanonicalDocument(
            String systemName) {

        if (systemName == null ||
                systemName.isBlank()) {

            return;
        }

        downstreamResponseCanonicalDocuments.remove(
                systemName.trim());
    }

    public Map<String, CanonicalDocument>
            getDownstreamResponseCanonicalDocuments() {

        return Map.copyOf(
                downstreamResponseCanonicalDocuments);
    }

    public CanonicalDocument
            getCommonResponseCanonicalDocument() {

        return commonResponseCanonicalDocument;
    }

    public void setCommonResponseCanonicalDocument(
            CanonicalDocument document) {

        validateDocument(
                document,
                CanonicalDocumentType.RESPONSE,
                "Common response");

        this.commonResponseCanonicalDocument =
                document;
    }

    public DownstreamState downstream(
            String systemName) {

        validateSystemName(systemName);

        return downstreamStates.computeIfAbsent(
                systemName.trim(),
                key -> new DownstreamState());
    }

    public Map<String, DownstreamState>
            getDownstreamStates() {

        return Map.copyOf(
                downstreamStates);
    }

    public Sinks.One<String>
            getCompletionSink() {

        return completionSink;
    }

    public String summary() {

        StringBuilder sb =
                new StringBuilder();

        sb.append("corr=")
                .append(correlationId)

                .append(" integration=")
                .append(integrationId)

                .append(" session=")
                .append(sessionId)

                .append(" terminal=")
                .append(terminalId)

                .append(" msgId=")
                .append(clientMsgId)

                .append(" gatewayMs=")
                .append(gatewayLatencyMillis());

        downstreamStates.forEach(
                (name, state) ->
                        sb.append(" ")
                                .append(name)
                                .append("Ms=")
                                .append(
                                        state.latencyMillis()));

        return sb.toString();
    }

    private void validateSystemName(
            String systemName) {

        if (systemName == null ||
                systemName.isBlank()) {

            throw new IllegalArgumentException(
                    "Downstream system name "
                            + "cannot be null or blank");
        }
    }

    private void validateDocument(
            CanonicalDocument document,
            CanonicalDocumentType expectedType,
            String description) {

        if (document == null) {

            throw new IllegalArgumentException(
                    description
                            + " canonical document "
                            + "cannot be null");
        }

        if (document.getDocumentType()
                != expectedType) {

            throw new IllegalArgumentException(
                    description
                            + " canonical document must "
                            + "be of type "
                            + expectedType);
        }
    }
}