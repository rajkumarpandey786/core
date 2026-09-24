package com.rkp.topcore.core.observability;

import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

public final class ObservabilityContext {

    private ObservabilityContext() {
    }

    /**
     * Executes the supplied Mono with the given LogContext
     * available through Reactor Context.
     */
    public static <T> Mono<T> withLogContext(
            LogContext logContext,
            Mono<T> mono) {

        return mono.contextWrite(
                reactorContext ->
                        reactorContext.put(
                                LogContextThreadLocalAccessor.KEY,
                                logContext));
    }

    /**
     * Retrieves the LogContext from Reactor Context.
     */
    public static LogContext get(
            ContextView contextView) {

        return contextView.getOrDefault(
                LogContextThreadLocalAccessor.KEY,
                null);
    }
}