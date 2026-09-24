package com.rkp.topcore.core.application;

import org.springframework.stereotype.Component;

import com.rkp.topcore.core.context.TopCoreContext;

@Component
public interface ApplicationRequestProcessor {

    void process(TopCoreContext context);
}