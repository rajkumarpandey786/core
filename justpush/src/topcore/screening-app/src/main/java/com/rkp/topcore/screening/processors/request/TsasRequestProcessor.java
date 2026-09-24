package com.rkp.topcore.screening.processors.request;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.plugin.api.downstream.DownstreamCanonicalProcessor;

@Component
public class TsasRequestProcessor implements DownstreamCanonicalProcessor {

	private static final String UNIT = "CPH";
	private static final String BUSINESS = "C400";
	private static final String APPLICATION = "CPH";
	private static final String SERVICE = "NA";
	private static final String USER_REF = "NA";
	private static final String DIRECTION = "I";

	private static final String TF_UNIT = "TF075";
	private static final String TF_BUSINESS = "TF076";
	private static final String TF_APPLICATION = "TF077";
	private static final String TF_SERVICE = "TF078";
	private static final String TF_USER_REF = "TF079";
	private static final String TF_DIRECTION = "TF080";
	private static final String TF_TRANSACTION_REFERENCE = "TF081";
	private static final String TF_EXTERNAL_TRANSACTION_ID = "TF004";
	private static final String TF_RETRIEVAL_REFERENCE_NUMBER = "TF023";

	@Override
	public String integrationId() {
		return "TSAS";
	}

	@Override
	public CanonicalDocument process(
			CanonicalDocument document) {

		if (document == null) {
			throw new IllegalArgumentException(
					"Canonical document cannot be null");
		}

		document.overrideOrAddTF(
				TF_UNIT,
				UNIT);

		document.overrideOrAddTF(
				TF_BUSINESS,
				BUSINESS);

		document.overrideOrAddTF(
				TF_APPLICATION,
				APPLICATION);

		document.overrideOrAddTF(
				TF_SERVICE,
				SERVICE);

		document.overrideOrAddTF(
				TF_USER_REF,
				USER_REF);

		document.overrideOrAddTF(
				TF_DIRECTION,
				DIRECTION);

		Object retrievalReference =
				document.getTF(
						TF_RETRIEVAL_REFERENCE_NUMBER);

		if (retrievalReference != null
				&& !retrievalReference.toString().isBlank()) {

			document.overrideOrAddTF(
					TF_TRANSACTION_REFERENCE,
					retrievalReference.toString());

		} else {

			document.overrideOrAddTF(
					TF_TRANSACTION_REFERENCE,
					UUID.randomUUID().toString());
		}

		Object externalTransactionId =
				document.getTF(
						TF_EXTERNAL_TRANSACTION_ID);

		if (externalTransactionId == null
				|| externalTransactionId.toString().isBlank()) {

			document.overrideOrAddTF(
					TF_EXTERNAL_TRANSACTION_ID,
					UUID.randomUUID().toString());
		}

		return document;
	}

}
