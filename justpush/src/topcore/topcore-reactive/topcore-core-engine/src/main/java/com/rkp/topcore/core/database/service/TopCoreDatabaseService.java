package com.rkp.topcore.core.database.service;

import java.time.LocalDateTime;

import com.rkp.topcore.core.database.model.MessageDirection;
import com.rkp.topcore.core.database.model.MessageStatus;
import com.rkp.topcore.core.database.model.TransactionStatus;

import reactor.core.publisher.Mono;

public interface TopCoreDatabaseService {

	Mono<Long> createTransaction(
			String correlationId,
			String messageId,
			String sessionId,
			String terminalId,
			String sourceSystem,
			TransactionStatus status,
			LocalDateTime startedAt,
			LocalDateTime createdAt
			);

	Mono<Void> saveMessage(
			Long transactionId,
			String systemName,
			MessageDirection direction,
			String messageType,
			String payload,
			String maskedPayload,
			MessageStatus status,
			Long latencyMs,
			LocalDateTime createdAt
			);

	Mono<Void> completeTransaction(
			Long transactionId,
			TransactionStatus status,
			LocalDateTime completedAt,
			Long latencyMs
			);
}