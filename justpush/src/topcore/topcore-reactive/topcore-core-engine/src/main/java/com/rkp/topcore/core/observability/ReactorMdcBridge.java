package com.rkp.topcore.core.observability;

import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

public final class ReactorMdcBridge {

    public static final String LOG_CONTEXT_KEY = "gateway.log.context";

    private ReactorMdcBridge() {
    }

    public static <T> Mono<T> withContext(
            Mono<T> mono,
            LogContext context) {

        return mono.contextWrite(ctx ->
                ctx.put(LOG_CONTEXT_KEY, context));
    }

    public static void apply(ContextView contextView) {

        if (contextView.hasKey(LOG_CONTEXT_KEY)) {

            LogContext context =
                    contextView.get(LOG_CONTEXT_KEY);

            LogContextHolder.set(context);
        }
    }

    public static void clear() {

        LogContextHolder.clear();
    }
}