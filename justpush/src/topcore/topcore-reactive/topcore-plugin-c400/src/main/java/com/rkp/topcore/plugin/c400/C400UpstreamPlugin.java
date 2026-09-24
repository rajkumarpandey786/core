package com.rkp.topcore.plugin.c400;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.engine.CanonicalEngine;
import com.rkp.topcore.core.xml.XmlCodec;
import com.rkp.topcore.plugin.api.upstream.UpstreamPlugin;
import com.rkp.topcore.plugin.api.upstream.UpstreamRoute;
import com.rkp.topcore.plugin.api.upstream.UpstreamTransactionMetadata;

@Component
public class C400UpstreamPlugin implements UpstreamPlugin {

    private final CanonicalEngine canonicalEngine;
    private final XmlCodec xmlCodec;
    private final C400Router router;

    public C400UpstreamPlugin(
            CanonicalEngine canonicalEngine,
            XmlCodec xmlCodec,
            C400Router router) {

        if (canonicalEngine == null) {
            throw new IllegalArgumentException(
                    "CanonicalEngine cannot be null");
        }

        if (xmlCodec == null) {
            throw new IllegalArgumentException(
                    "XmlCodec cannot be null");
        }

        if (router == null) {
            throw new IllegalArgumentException(
                    "C400Router cannot be null");
        }

        this.canonicalEngine = canonicalEngine;
        this.xmlCodec = xmlCodec;
        this.router = router;
    }

    @Override
    public String integrationId() {
        return "C400";
    }

    @Override
    public UpstreamRoute route(String request) {
        return router.route(request);
    }

    @Override
    public CanonicalDocument toCanonical(
            String request) {

        if (request == null || request.isBlank()) {
            throw new IllegalArgumentException(
                    "C400 request cannot be null or blank");
        }

        Map<String, String> parsedData =
                xmlCodec.parse(request);

        Map<String, Object> sourceData =
                new java.util.LinkedHashMap<>(
                        parsedData);

        return canonicalEngine.createInbound(
                integrationId(),
                sourceData);
    }
    
    @Override
    public UpstreamTransactionMetadata transactionMetadata(
            String requestPayload) {

        if (requestPayload == null || requestPayload.isBlank()) {
            throw new IllegalArgumentException(
                    "C400 request cannot be null or blank");
        }

        Map<String, String> parsedData =
                xmlCodec.parse(requestPayload);

        String transactionId =
                parsedData.get(
                        "root.rec.externalTransactionId");

        String terminalId =
                parsedData.get(
                        "root.rec.TerminalID");

        return new UpstreamTransactionMetadata(
                transactionId,
                terminalId);
    }

    @Override
    public String fromCanonical(
            CanonicalDocument response) {

        if (response == null) {
            throw new IllegalArgumentException(
                    "Canonical response cannot be null");
        }

        Map<String, Object> output =
                canonicalEngine.createOutbound(
                        response,
                        integrationId());

        return xmlCodec.build(output);
    }
}