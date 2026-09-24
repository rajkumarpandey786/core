package com.rkp.topcore.core.observability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GatewayLogger {

    private GatewayLogger() {
    }

    private static String prefix() {

        LogContext context =
                LogContextHolder.get();

        if (context == null) {
            return "";
        }

        return "seq=" +
                context.nextSequence() +
                " ";
    }

    public static Logger getLogger(
            Class<?> type) {

        return LoggerFactory.getLogger(type);
    }

    public static void info(
            Logger logger,
            String message,
            Object... args) {

        if (!logger.isInfoEnabled()) {
            return;
        }

        logger.info(
                prefix() + message,
                args);
    }

    public static void debug(
            Logger logger,
            String message,
            Object... args) {

        if (!logger.isDebugEnabled()) {
            return;
        }

        logger.debug(
                prefix() + message,
                args);
    }

    public static void warn(
            Logger logger,
            String message,
            Object... args) {

        if (!logger.isWarnEnabled()) {
            return;
        }

        logger.warn(
                prefix() + message,
                args);
    }

    public static void error(
            Logger logger,
            String message,
            Object... args) {

        if (!logger.isErrorEnabled()) {
            return;
        }

        logger.error(
                prefix() + message,
                args);
    }
}