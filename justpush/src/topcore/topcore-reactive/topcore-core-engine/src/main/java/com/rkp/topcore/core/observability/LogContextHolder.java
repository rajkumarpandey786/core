package com.rkp.topcore.core.observability;

import org.slf4j.MDC;

public final class LogContextHolder {

    private static final ThreadLocal<LogContext> HOLDER =
            new ThreadLocal<>();

    private LogContextHolder() {
    }

    public static void set(LogContext context) {

        HOLDER.set(context);

        MDC.put("corr", context.getCorrelationId());

        if (context.getSessionId() != null) {
            MDC.put("session", context.getSessionId());
        }

        if (context.getTerminalId() != null) {
            MDC.put("terminal", context.getTerminalId());
        }

        if (context.getClientMsgId() != null) {
            MDC.put("msgId", context.getClientMsgId());
        }
    }

    public static LogContext get() {
        return HOLDER.get();
    }

    public static void clear() {

        HOLDER.remove();

        MDC.clear();
    }

    public static void update() {

        LogContext context = HOLDER.get();

        if (context == null) {
            return;
        }

        MDC.put("corr", context.getCorrelationId());

        if (context.getSessionId() != null) {
            MDC.put("session", context.getSessionId());
        }

        if (context.getTerminalId() != null) {
            MDC.put("terminal", context.getTerminalId());
        }

        if (context.getClientMsgId() != null) {
            MDC.put("msgId", context.getClientMsgId());
        }
    }
}