package com.rkp.topcore.screening.processors.response;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.core.application.ApplicationResponseProcessor;
import com.rkp.topcore.core.config.GatewayConfig;
import com.rkp.topcore.core.context.TopCoreContext;
import com.rkp.topcore.core.observability.GatewayLogger;

@Component
public class C400FinalMergedResponseProcessor
implements ApplicationResponseProcessor {

	private static final Logger log =
			GatewayLogger.getLogger(
					C400FinalMergedResponseProcessor.class);

	private static final String TSAS =
			"TSAS";

	private static final String PAIMI =
			"PAIMI";

	private static final String CHANNEL =
			"TF005";

	private static final String TRANSACTION_REFERENCE =
			"TF004";

	private static final String RESPONSE_CODE =
			"TF006";

	private static final String RESPONSE_MESSAGE =
			"TF007";

	private static final String ERROR_CODE =
			"TF008";

	private static final String ERROR_DESCRIPTION =
			"TF009";

	private static final String DOWNSTREAM_RESPONSE_CODE =
			"TF006";

	private static final String DOWNSTREAM_RESPONSE_DESCRIPTION =
			"TF010";

	private final GatewayConfig gatewayConfig;

	public C400FinalMergedResponseProcessor(
			GatewayConfig gatewayConfig) {

		if (gatewayConfig == null) {
			throw new IllegalArgumentException(
					"GatewayConfig cannot be null");
		}

		this.gatewayConfig = gatewayConfig;
	}

	@Override
	public void process(
			TopCoreContext context) {

		if (context == null) {
			throw new IllegalArgumentException(
					"TopCoreContext cannot be null");
		}

		GatewayLogger.info(log,
				"Final response merge started");

		Map<String, CanonicalDocument>
		downstreamResponses =
		context.getDownstreamResponseCanonicalDocuments();

		if (downstreamResponses == null) {
			throw new IllegalStateException(
					"Downstream response map cannot be null");
		}

		CanonicalDocument commonResponse =
				context.getCommonResponseCanonicalDocument();

		if (commonResponse == null) {
			throw new IllegalStateException(
					"Common response CanonicalDocument "
							+ "has not been initialized");
		}

		List<String> activeDownstreams =
				gatewayConfig
				.getDownstream()
				.getActive();

		if (activeDownstreams == null ||
				activeDownstreams.isEmpty()) {

			throw new IllegalStateException(
					"No active downstream systems configured");
		}

		CanonicalDocument tsasResponse =
				downstreamResponses.get(TSAS);

		CanonicalDocument paimiResponse =
				downstreamResponses.get(PAIMI);

		boolean tsasActive =
				activeDownstreams.contains(TSAS);

		boolean paimiActive =
				activeDownstreams.contains(PAIMI);

		boolean tsasSuccess =
				!tsasActive ||
				isTsasSuccessful(tsasResponse);

		boolean paimiSuccess =
				!paimiActive ||
				isPaimiSuccessful(paimiResponse);

		GatewayLogger.info(log,
				"Downstream evaluation completed "
						+ "active={} "
						+ "TSAS active={} success={} "
						+ "PAIMI active={} success={}",
						activeDownstreams,
						tsasActive,
						tsasActive && tsasSuccess,
						paimiActive,
						paimiActive && paimiSuccess);

		if (tsasActive && paimiActive) {

			handleBothActiveResponse(
					commonResponse,
					tsasResponse,
					paimiResponse,
					tsasSuccess,
					paimiSuccess);

		} else if (tsasActive) {

			handleTsasOnlyResponse(
					commonResponse,
					tsasResponse,
					tsasSuccess);

		} else if (paimiActive) {

			handlePaimiOnlyResponse(
					commonResponse,
					paimiResponse,
					paimiSuccess);

		} else {

			throw new IllegalStateException(
					"No supported downstream systems are active");
		}

		GatewayLogger.info(log,
				"Common response after merge={}",
				commonResponse.getAsJson());

		GatewayLogger.info(log,
				"Final response merge completed");
	}

	private void handleTsasOnlyResponse(
			CanonicalDocument commonResponse,
			CanonicalDocument tsasResponse,
			boolean tsasSuccess) {

		if (tsasSuccess) {

			String transactionReference =
					resolveTransactionReference(
							tsasResponse,
							null);

			applySuccessResponse(
					commonResponse,
					transactionReference);

			return;
		}

		handleFailureResponse(
				commonResponse,
				tsasResponse,
				null,
				false,
				true);
	}

	private void handlePaimiOnlyResponse(
			CanonicalDocument commonResponse,
			CanonicalDocument paimiResponse,
			boolean paimiSuccess) {

		if (paimiSuccess) {

			String transactionReference =
					resolveTransactionReference(
							null,
							paimiResponse);

			applySuccessResponse(
					commonResponse,
					transactionReference);

			return;
		}

		handleFailureResponse(
				commonResponse,
				null,
				paimiResponse,
				true,
				false);
	}

	private void handleBothActiveResponse(
			CanonicalDocument commonResponse,
			CanonicalDocument tsasResponse,
			CanonicalDocument paimiResponse,
			boolean tsasSuccess,
			boolean paimiSuccess) {

		if (tsasSuccess && paimiSuccess) {

			String transactionReference =
					resolveTransactionReference(
							tsasResponse,
							paimiResponse);

			applySuccessResponse(
					commonResponse,
					transactionReference);

			return;
		}

		handleFailureResponse(
				commonResponse,
				tsasResponse,
				paimiResponse,
				tsasSuccess,
				paimiSuccess);
	}

	private void applySuccessResponse(
			CanonicalDocument commonResponse,
			String transactionReference) {

		if (transactionReference != null &&
				!transactionReference.isBlank()) {

			commonResponse.overrideOrAddTF(
					TRANSACTION_REFERENCE,
					transactionReference);
		}

		commonResponse.overrideOrAddTF(
				RESPONSE_CODE,
				"00");

		commonResponse.overrideOrAddTF(
				RESPONSE_MESSAGE,
				"APPROVED");

		commonResponse.overrideOrAddTF(
				ERROR_CODE,
				"");

		commonResponse.overrideOrAddTF(
				ERROR_DESCRIPTION,
				"");

		GatewayLogger.info(log,
				"Final business decision=APPROVED "
						+ "responseCode={} responseMessage={}",
						commonResponse.getTF(RESPONSE_CODE),
						commonResponse.getTF(RESPONSE_MESSAGE));
	}

	private void handleFailureResponse(
			CanonicalDocument commonResponse,
			CanonicalDocument tsasResponse,
			CanonicalDocument paimiResponse,
			boolean tsasSuccess,
			boolean paimiSuccess) {

		commonResponse.overrideOrAddTF(
				RESPONSE_CODE,
				"99");

		commonResponse.overrideOrAddTF(
				RESPONSE_MESSAGE,
				"DECLINED");

		String errorDescription =
				buildFailureDescription(
						tsasResponse,
						paimiResponse,
						tsasSuccess,
						paimiSuccess);

		commonResponse.overrideOrAddTF(
				ERROR_DESCRIPTION,
				errorDescription);

		commonResponse.overrideOrAddTF(
				ERROR_CODE,
				"DOWNSTREAM_FAILURE");

		GatewayLogger.warn(log,
				"Final business decision=DECLINED "
						+ "errorCode={} errorDescription={}",
						commonResponse.getTF(ERROR_CODE),
						commonResponse.getTF(ERROR_DESCRIPTION));
	}

	private boolean isTsasSuccessful(
			CanonicalDocument response) {

		return isSuccessfulResponse(response);
	}

	private boolean isPaimiSuccessful(
			CanonicalDocument response) {

		return isSuccessfulResponse(response);
	}

	private boolean isSuccessfulResponse(
			CanonicalDocument response) {

		if (response == null) {
			return false;
		}

		Object responseCode =
				response.getTF(
						DOWNSTREAM_RESPONSE_CODE);

		if (responseCode == null) {
			return false;
		}

		String code =
				String.valueOf(responseCode);

		return "000".equals(code)
				|| "200".equals(code)
				|| "00".equals(code);
	}

	private String resolveTransactionReference(
			CanonicalDocument tsasResponse,
			CanonicalDocument paimiResponse) {

		String tsasReference =
				getStringValue(
						tsasResponse,
						TRANSACTION_REFERENCE);

		if (tsasReference != null &&
				!tsasReference.isBlank()) {

			return tsasReference;
		}

		String paimiReference =
				getStringValue(
						paimiResponse,
						TRANSACTION_REFERENCE);

		if (paimiReference != null &&
				!paimiReference.isBlank()) {

			return paimiReference;
		}

		return null;
	}

	private String buildFailureDescription(
			CanonicalDocument tsasResponse,
			CanonicalDocument paimiResponse,
			boolean tsasSuccess,
			boolean paimiSuccess) {

		StringBuilder error =
				new StringBuilder();

		if (!tsasSuccess) {

			appendFailure(
					error,
					TSAS,
					tsasResponse);
		}

		if (!paimiSuccess) {

			if (error.length() > 0) {
				error.append("; ");
			}

			appendFailure(
					error,
					PAIMI,
					paimiResponse);
		}

		return error.toString();
	}

	private void appendFailure(
			StringBuilder error,
			String systemName,
			CanonicalDocument response) {

		error.append(systemName);

		error.append(
				" failed");

		if (response == null) {

			error.append(
					" - no response received");

			return;
		}

		String description =
				getStringValue(
						response,
						DOWNSTREAM_RESPONSE_DESCRIPTION);

		if (description != null &&
				!description.isBlank()) {

			error.append(
					" - ");

			error.append(
					description);
		}

		String responseCode =
				getStringValue(
						response,
						DOWNSTREAM_RESPONSE_CODE);

		if (responseCode != null &&
				!responseCode.isBlank()) {

			error.append(
					" [");

			error.append(
					responseCode);

			error.append(
					"]");
		}
	}

	private String getStringValue(
			CanonicalDocument document,
			String field) {

		if (document == null) {
			return null;
		}

		Object value =
				document.getTF(field);

		if (value == null) {
			return null;
		}

		return String.valueOf(value);
	}
}
