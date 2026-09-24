package com.rkp.topcore.core.response;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.rkp.topcore.canonical.definition.CanonicalConfiguration;
import com.rkp.topcore.canonical.definition.CanonicalFieldDefinition;
import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.document.CanonicalDocumentFactory;
import com.rkp.topcore.core.context.TopCoreContext;
import com.rkp.topcore.core.observability.GatewayLogger;

@Component
public class CommonResponseInitializer {

    private static final Logger log =
            LoggerFactory.getLogger(
                    CommonResponseInitializer.class
            );

    private final CanonicalDocumentFactory documentFactory;

    private final CanonicalConfiguration canonicalConfiguration;


    public CommonResponseInitializer(
            CanonicalDocumentFactory documentFactory,
            CanonicalConfiguration canonicalConfiguration) {

        if (documentFactory == null) {

            throw new IllegalArgumentException(
                    "CanonicalDocumentFactory cannot be null"
            );
        }

        if (canonicalConfiguration == null) {

            throw new IllegalArgumentException(
                    "CanonicalConfiguration cannot be null"
            );
        }

        this.documentFactory =
                documentFactory;

        this.canonicalConfiguration =
                canonicalConfiguration;
    }


    public CanonicalDocument initialize(
            TopCoreContext context) {

        if (context == null) {

            throw new IllegalArgumentException(
                    "TopCoreContext cannot be null"
            );
        }

        GatewayLogger.info(
                log,
                "Common response initialization started correlationId={}",
                context.getCorrelationId()
        );


        CanonicalDocument request =
                context.getRequestCanonicalDocument();

        if (request == null) {

            GatewayLogger.error(
                    log,
                    "Request CanonicalDocument is missing during common response initialization correlationId={}",
                    context.getCorrelationId()
            );

            throw new IllegalStateException(
                    "Request CanonicalDocument is not available"
            );
        }


        CanonicalDocument commonResponse =
                documentFactory.createResponse();


        GatewayLogger.debug(
                log,
                "Empty common response CanonicalDocument created correlationId={}",
                context.getCorrelationId()
        );


        int copiedFieldCount = 0;


        for (Map.Entry<String, CanonicalFieldDefinition> entry :
                canonicalConfiguration
                        .getInboundFields()
                        .entrySet()) {

            String fieldId =
                    entry.getKey();

            CanonicalFieldDefinition definition =
                    entry.getValue();


            if (definition == null ||
                    !definition.isCopyToResponse()) {

                continue;
            }


            Object value =
                    request.getTF(fieldId);


            if (value == null) {

                GatewayLogger.debug(
                        log,
                        "Common response field skipped field={} reason=request value is null correlationId={}",
                        fieldId,
                        context.getCorrelationId()
                );

                continue;
            }


            commonResponse.addTF(
                    fieldId,
                    value
            );

            copiedFieldCount++;


            GatewayLogger.info(
                    log,
                    "Common response field copied field={} name={} value={} correlationId={}",
                    fieldId,
                    definition.getName(),
                    value,
                    context.getCorrelationId()
            );
        }


        context.setCommonResponseCanonicalDocument(
                commonResponse
        );


        GatewayLogger.info(
                log,
                "Common response initialization completed copiedFields={} correlationId={}",
                copiedFieldCount,
                context.getCorrelationId()
        );


        GatewayLogger.debug(
                log,
                "Initialized common response CanonicalDocument={} correlationId={}",
                commonResponse,
                context.getCorrelationId()
        );


        return commonResponse;
    }


    public Map<String, CanonicalDocument>
            getDownstreamResponses(
                    TopCoreContext context) {

        if (context == null) {

            throw new IllegalArgumentException(
                    "TopCoreContext cannot be null"
            );
        }

        return context
                .getDownstreamResponseCanonicalDocuments();
    }


    public CanonicalDocument getCommonResponse(
            TopCoreContext context) {

        if (context == null) {

            throw new IllegalArgumentException(
                    "TopCoreContext cannot be null"
            );
        }

        return context
                .getCommonResponseCanonicalDocument();
    }
}