package com.rkp.topcore.canonical.processor;

import java.util.Map;
import java.util.function.Consumer;

import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.engine.InboundMappingEngine;
import com.rkp.topcore.canonical.engine.OutboundMappingEngine;
import com.rkp.topcore.canonical.mapping.MappingConfigurationResolver;
import com.rkp.topcore.canonical.runtime.RuntimeMapping;

public final class CanonicalProcessor {

    private final InboundMappingEngine inboundMappingEngine;

    private final OutboundMappingEngine outboundMappingEngine;

    private final MappingConfigurationResolver resolver;


    public CanonicalProcessor(
            InboundMappingEngine inboundMappingEngine,
            OutboundMappingEngine outboundMappingEngine,
            MappingConfigurationResolver resolver) {

        if (inboundMappingEngine == null) {

            throw new IllegalArgumentException(
                    "InboundMappingEngine cannot be null"
            );
        }

        if (outboundMappingEngine == null) {

            throw new IllegalArgumentException(
                    "OutboundMappingEngine cannot be null"
            );
        }

        if (resolver == null) {

            throw new IllegalArgumentException(
                    "MappingConfigurationResolver cannot be null"
            );
        }

        this.inboundMappingEngine =
                inboundMappingEngine;

        this.outboundMappingEngine =
                outboundMappingEngine;

        this.resolver =
                resolver;
    }


    public CanonicalDocument inbound(
            String upstreamId,
            Map<String, Object> sourceData,
            CanonicalDocument document) {

        RuntimeMapping mapping =
                resolver.resolveInbound(
                        upstreamId
                );

        return inboundMappingEngine.map(
                sourceData,
                mapping,
                document
        );
    }


    public Map<String, Object> outbound(
            String downstreamId,
            CanonicalDocument document) {

        RuntimeMapping mapping =
                resolver.resolveOutbound(
                        downstreamId
                );

        return outboundMappingEngine.map(
                document,
                mapping
        );
    }


    public CanonicalDocument processInbound(
            String upstreamId,
            Map<String, Object> sourceData,
            CanonicalDocument document,
            Consumer<CanonicalDocument> businessProcessor) {

        CanonicalDocument canonical =
                inbound(
                        upstreamId,
                        sourceData,
                        document
                );

        if (businessProcessor != null) {

            businessProcessor.accept(
                    canonical
            );
        }

        return canonical;
    }
}