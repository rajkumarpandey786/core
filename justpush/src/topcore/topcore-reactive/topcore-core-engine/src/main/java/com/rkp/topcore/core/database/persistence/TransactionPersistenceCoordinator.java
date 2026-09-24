package com.rkp.topcore.core.database.persistence;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.rkp.topcore.core.context.TopCoreContext;
import com.rkp.topcore.core.database.config.DatabaseProperties;
import com.rkp.topcore.core.database.model.TransactionStatus;
import com.rkp.topcore.core.database.service.TopCoreDatabaseService;

import reactor.core.publisher.Mono;

@Component
public class TransactionPersistenceCoordinator {

	private final TopCoreDatabaseService databaseService;
	private final DatabaseProperties databaseProperties;

	public TransactionPersistenceCoordinator(
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

	public Mono<Long> start(
			TopCoreContext context,
			String sourceSystem) {

		if (context == null) {
			return Mono.error(
					new IllegalArgumentException(
							"TopCoreContext cannot be null"
							)
					);
		}

		if (!isTransactionPersistenceEnabled()) {
			return Mono.empty();
		}

		LocalDateTime now =
				LocalDateTime.now();

		return databaseService
				.createTransaction(
						String.valueOf(
								context.getCorrelationId()
								),
						context.getClientMsgId(),
						context.getSessionId(),
						context.getTerminalId(),
						sourceSystem,
						TransactionStatus.STARTED,
						now,
						now
						)
				.doOnNext(
						context::setDatabaseTransactionId
						);
	}

	public Mono<Void> complete(
			TopCoreContext context,
			TransactionStatus status) {

		if (context == null) {
			return Mono.error(
					new IllegalArgumentException(
							"TopCoreContext cannot be null"
							)
					);
		}

		if (!isTransactionPersistenceEnabled()) {
			return Mono.empty();
		}

		Long transactionId =
				context.getDatabaseTransactionId();

		if (transactionId == null) {
			return Mono.error(
					new IllegalStateException(
							"Database transaction ID is missing"
							)
					);
		}

		return databaseService.completeTransaction(
				transactionId,
				status,
				LocalDateTime.now(),
				context.gatewayLatencyMillis()
				);
	}

	private boolean isTransactionPersistenceEnabled() {

		return databaseProperties
				.getPersistence()
				.getTransaction()
				.isEnabled();
	}
}