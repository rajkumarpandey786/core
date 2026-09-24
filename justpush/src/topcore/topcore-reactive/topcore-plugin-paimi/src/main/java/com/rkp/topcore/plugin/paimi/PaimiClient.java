package com.rkp.topcore.plugin.paimi;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.engine.CanonicalEngine;
import com.rkp.topcore.core.config.GatewayConfig;
import com.rkp.topcore.core.context.TopCoreContext;
import com.rkp.topcore.core.downstream.DownstreamClient;
import com.rkp.topcore.core.downstream.WebClientFactory;

import reactor.core.publisher.Mono;

@Component
public class PaimiClient implements DownstreamClient {

	private final WebClient webClient;
	private final GatewayConfig config;
	private final CanonicalEngine canonicalEngine;
	private final ObjectMapper objectMapper;

	public PaimiClient(
			WebClientFactory webClientFactory,
			GatewayConfig config,
			CanonicalEngine canonicalEngine,
			ObjectMapper objectMapper) {

		if (webClientFactory == null) {
			throw new IllegalArgumentException(
					"WebClientFactory cannot be null");
		}

		if (config == null) {
			throw new IllegalArgumentException(
					"GatewayConfig cannot be null");
		}

		if (canonicalEngine == null) {
			throw new IllegalArgumentException(
					"CanonicalEngine cannot be null");
		}

		if (objectMapper == null) {
			throw new IllegalArgumentException(
					"ObjectMapper cannot be null");
		}

		this.webClient =
				webClientFactory.create("PAIMI");

		this.config =
				config;

		this.canonicalEngine =
				canonicalEngine;

		this.objectMapper =
				objectMapper;
	}

	@Override
	public String integrationId() {
		return "PAIMI";
	}

	@Override
	public Mono<Void> call(
			TopCoreContext context) {

		if (context == null) {
			return Mono.error(
					new IllegalArgumentException(
							"TopCoreContext cannot be null"));
		}

		String system =
				integrationId();

		return Mono.defer(() -> {

			CanonicalDocument document =
					context.getDownstreamRequestCanonicalDocument(
							system);

			if (document == null) {
				return Mono.error(
						new IllegalStateException(
								"PAIMI request canonical document "
										+ "is missing"));
			}

			final String requestJson;

			try {

				Map<String, Object> request =
						canonicalEngine.createOutbound(
								document,
								system);

				requestJson =
						objectMapper.writeValueAsString(
								request);

			} catch (Exception ex) {
				return Mono.error(ex);
			}

			GatewayConfig.Downstream.SystemConfig
			systemConfig =
			config.getDownstream()
			.getSystems()
			.get(system);

			if (systemConfig == null) {
				return Mono.error(
						new IllegalStateException(
								"No configuration found for "
										+ "downstream "
										+ system));
			}

			context.downstream(system)
			.markStart();

			return webClient.post()
					.uri(
							systemConfig.getUrl()
							+ "/api/process")
					.contentType(
							MediaType.APPLICATION_JSON)
					.bodyValue(requestJson)
					.retrieve()
					.bodyToMono(
							new ParameterizedTypeReference<
							Map<String, Object>>() {})
					.flatMap(response -> {

						context.downstream(system)
						.markEnd();

						final String responseJson;

						try {

							responseJson =
									objectMapper.writeValueAsString(
											response);

						} catch (Exception ex) {
							return Mono.error(ex);
						}

						context.downstream(system)
						.setResponsePayload(
								responseJson);

						CanonicalDocument responseDocument;

						try {

							responseDocument =
							        canonicalEngine.createResponse(
							                system,
							                response);

						} catch (Exception ex) {
							return Mono.error(ex);
						}

						context.setDownstreamResponseCanonicalDocument(
								system,
								responseDocument);

						return Mono.<Void>empty();
					})
					.then();
		});
	}

}
