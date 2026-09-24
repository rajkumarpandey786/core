package com.rkp.topcore.core.observability;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class TransactionAuditLogger {

    private static final Logger log =
            GatewayLogger.getLogger(TransactionAuditLogger.class);

    public void logRequest(LogContext context,
                           String payload) {

        log.info(
                "AUDIT REQUEST corr={} session={} terminal={} msgId={} payload={}",
                context.getCorrelationId(),
                context.getSessionId(),
                context.getTerminalId(),
                context.getClientMsgId(),
                payload);
    }

    public void logResponse(LogContext context,
                            String payload,
                            long latencyMs) {

        log.info(
                "AUDIT RESPONSE corr={} session={} terminal={} msgId={} latency={}ms payload={}",
                context.getCorrelationId(),
                context.getSessionId(),
                context.getTerminalId(),
                context.getClientMsgId(),
                latencyMs,
                payload);
    }

    public void logTimeout(LogContext context,
                           long latencyMs) {

        log.warn(
                "AUDIT TIMEOUT corr={} session={} terminal={} msgId={} latency={}ms",
                context.getCorrelationId(),
                context.getSessionId(),
                context.getTerminalId(),
                context.getClientMsgId(),
                latencyMs);
    }

    public void logFailure(LogContext context,
                           String reason) {

        log.error(
                "AUDIT FAILURE corr={} session={} terminal={} msgId={} reason={}",
                context.getCorrelationId(),
                context.getSessionId(),
                context.getTerminalId(),
                context.getClientMsgId(),
                reason);
    }
}