package com.rkp.topcore.core.observability;

import io.micrometer.context.ThreadLocalAccessor;

public class LogContextThreadLocalAccessor
        implements ThreadLocalAccessor<LogContext> {

    public static final String KEY =
            "gateway.log.context";

    @Override
    public Object key() {
        return KEY;
    }

    @Override
    public LogContext getValue() {
        return LogContextHolder.get();
    }

    @Override
    public void setValue(LogContext value) {
        LogContextHolder.set(value);
    }

    @Override
    public void reset() {
        LogContextHolder.clear();
    }
}