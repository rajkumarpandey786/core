package com.rkp.topcore.core.observability;

import org.springframework.context.annotation.Configuration;

import io.micrometer.context.ContextRegistry;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Hooks;

@Configuration
public class ObservabilityConfig {

    @PostConstruct
    public void initialize() {

        ContextRegistry.getInstance()
                .registerThreadLocalAccessor(
                        new LogContextThreadLocalAccessor());

        Hooks.enableAutomaticContextPropagation();
    }
}