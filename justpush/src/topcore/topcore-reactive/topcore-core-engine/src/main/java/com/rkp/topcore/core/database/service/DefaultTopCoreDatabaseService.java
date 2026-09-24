package com.rkp.topcore.core.database.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.rkp.topcore.core.database.model.MessageDirection;
import com.rkp.topcore.core.database.model.MessageRecord;
import com.rkp.topcore.core.database.model.MessageStatus;
import com.rkp.topcore.core.database.model.TransactionRecord;
import com.rkp.topcore.core.database.model.TransactionStatus;
import com.rkp.topcore.core.database.repository.MessageRepository;
import com.rkp.topcore.core.database.repository.TransactionRepository;

import reactor.core.publisher.Mono;

@Service
public class DefaultTopCoreDatabaseService
implements TopCoreDatabaseService {

	private final TransactionRepository transactionRepository;

	private final MessageRepository messageRepository;

	public DefaultTopCoreDatabaseService(
			TransactionRepository transactionRepository,
			MessageRepository messageRepository) {

		if (transactionRepository == null) {
			throw new IllegalArgumentException(
					"TransactionRepository cannot be null"
					);
		}

		if (messageRepository == null) {
			throw new IllegalArgumentException(
					"MessageRepository cannot be null"
					);
		}

		this.transactionRepository =
				transactionRepository;

		this.messageRepository =
				messageRepository;
	}

	@Override
	public Mono<Long> createTransaction(
			String correlationId,
			String messageId,
			String sessionId,
			String terminalId,
			String sourceSystem,
			TransactionStatus status,
			LocalDateTime startedAt,
			LocalDateTime createdAt) {

		TransactionRecord transaction =
				new TransactionRecord();

		transaction.setCorrelationId(
				correlationId
				);

		transaction.setMessageId(
				messageId
				);

		transaction.setSessionId(
				sessionId
				);

		transaction.setTerminalId(
				terminalId
				);

		transaction.setSourceSystem(
				sourceSystem
				);

		transaction.setStatus(
				status != null
				? status.name()
						: null
				);

		transaction.setStartedAt(
				startedAt
				);

		transaction.setCreatedAt(
				createdAt
				);

		return transactionRepository.save(
				transaction
				);
	}

	@Override
	public Mono<Void> saveMessage(
			Long transactionId,
			String systemName,
			MessageDirection direction,
			String messageType,
			String payload,
			String maskedPayload,
			MessageStatus status,
			Long latencyMs,
			LocalDateTime createdAt) {

		MessageRecord message =
				new MessageRecord();

		message.setTransactionId(
				transactionId
				);

		message.setSystemName(
				systemName
				);

		message.setDirection(
				direction != null
				? direction.name()
						: null
				);

		message.setMessageType(
				messageType
				);

		message.setPayload(
				payload
				);

		message.setMaskedPayload(
				maskedPayload
				);

		message.setStatus(
				status != null
				? status.name()
						: null
				);

		message.setLatencyMs(
				latencyMs
				);

		message.setCreatedAt(
				createdAt
				);

		return messageRepository.save(
				message
				);
	}

	@Override
	public Mono<Void> completeTransaction(
			Long transactionId,
			TransactionStatus status,
			LocalDateTime completedAt,
			Long latencyMs) {

		return transactionRepository.complete(
				transactionId,
				status != null
				? status.name()
						: null,
						completedAt,
						latencyMs
				);
	}
}