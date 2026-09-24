package com.rkp.topcore.screening.processors.request;

import org.springframework.stereotype.Component;

import com.rkp.topcore.core.application.ApplicationRequestProcessor;
import com.rkp.topcore.core.context.TopCoreContext;

@Component
public class C400RequestProcessor
        implements ApplicationRequestProcessor {

    @Override
    public void process(TopCoreContext context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "TopCoreContext cannot be null");
        }
    }
}
/***
 *  50 plugins
 * */

