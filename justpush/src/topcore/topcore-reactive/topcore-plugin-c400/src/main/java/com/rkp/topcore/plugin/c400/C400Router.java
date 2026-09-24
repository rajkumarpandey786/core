package com.rkp.topcore.plugin.c400;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.rkp.topcore.core.xml.XmlCodec;
import com.rkp.topcore.plugin.api.upstream.UpstreamRoute;
import com.rkp.topcore.plugin.api.upstream.UpstreamRouter;

@Component
public class C400Router implements UpstreamRouter {

    private static final String ECHO = "ECHO";

    private final XmlCodec xmlCodec;

    public C400Router(XmlCodec xmlCodec) {

        if (xmlCodec == null) {
            throw new IllegalArgumentException(
                    "XmlCodec cannot be null");
        }

        this.xmlCodec = xmlCodec;
    }

    @Override
    public UpstreamRoute route(String request) {

        if (request == null || request.isBlank()) {
            return UpstreamRoute.unknown();
        }

        Map<String, String> parsedData =
                xmlCodec.parse(request);

        if (parsedData == null ||
                parsedData.isEmpty()) {

            return UpstreamRoute.unknown();
        }

        String echo =
                parsedData.get("root.rec.Echo");

        if (ECHO.equalsIgnoreCase(echo)) {
            return UpstreamRoute.control(ECHO);
        }

        String transactionId =
                parsedData.get(
                        "root.rec.externalTransactionId");

        if (transactionId != null &&
                !transactionId.isBlank()) {

            return UpstreamRoute.transaction();
        }

        return UpstreamRoute.unknown();
    }
}