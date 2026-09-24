package com.rkp.topcore.core.database.persistence;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.rkp.topcore.core.context.TopCoreContext;
import com.rkp.topcore.core.database.config.DatabaseProperties;
import com.rkp.topcore.core.database.model.MessageDirection;
import com.rkp.topcore.core.database.model.MessageStatus;
import com.rkp.topcore.core.database.service.TopCoreDatabaseService;

import reactor.core.publisher.Mono;

@Component
public class MessagePersistenceCoordinator {

	private final TopCoreDatabaseService databaseService;
	private final DatabaseProperties databaseProperties;

	public MessagePersistenceCoordinator(
			TopCoreDatabaseService databaseService,
			DatabaseProperties databaseProperties) {

		if (databaseService == null) {
			throw new IllegalArgumentException(
					"TopCoreDatabaseService cannot be null"
					);
		}

		if (databaseProperties == null) {
			throw new IllegalArgumentException(
					"DatabaseProperties cannot be null"
					);
		}

		this.databaseService = databaseService;
		this.databaseProperties = databaseProperties;
	}

	public Mono<Void> save(
			TopCoreContext context,
			String systemName,
			MessageDirection direction,
			String messageType,
			String payload,
			String maskedPayload,
			MessageStatus status,
			Long latencyMs) {

		if (context == null) {
			return Mono.error(
					new IllegalArgumentException(
							"TopCoreContext cannot be null"
							)
					);
		}

		if (!isMessagePersistenceEnabled()) {
			return Mono.empty();
		}

		return Mono.defer(() -> {

			Long transactionId =
					context.getDatabaseTransactionId();

			if (transactionId == null) {
				return Mono.error(
						new IllegalStateException(
								"Database transaction ID is missing"
								)
						);
			}

			return databaseService.saveMessage(
					transactionId,
					systemName,
					direction,
					messageType,
					payload,
					maskedPayload,
					status,
					latencyMs,
					LocalDateTime.now()
					);
		});
	}

	private boolean isMessagePersistenceEnabled() {
		return databaseProperties
				.getPersistence()
				.getMessage()
				.isEnabled();
	}
}