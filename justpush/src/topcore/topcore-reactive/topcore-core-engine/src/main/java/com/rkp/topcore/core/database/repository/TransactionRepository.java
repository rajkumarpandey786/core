package com.rkp.topcore.core.database.repository;

import java.time.LocalDateTime;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.rkp.topcore.core.database.model.TransactionRecord;

import reactor.core.publisher.Mono;

@Repository
public class TransactionRepository {

    private final DatabaseClient databaseClient;

    public TransactionRepository(DatabaseClient databaseClient) {

        if (databaseClient == null) {
            throw new IllegalArgumentException(
                    "DatabaseClient cannot be null"
            );
        }

        this.databaseClient = databaseClient;
    }

    public Mono<Long> save(TransactionRecord transaction) {

        if (transaction == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "TransactionRecord cannot be null"
                    )
            );
        }

        DatabaseClient.GenericExecuteSpec spec =
                databaseClient.sql(
                        """
                        INSERT INTO topcore_transaction (
                            correlation_id,
                            message_id,
                            session_id,
                            terminal_id,
                            source_system,
                            status,
                            started_at,
                            created_at
                        )
                        VALUES (
                            :correlationId,
                            :messageId,
                            :sessionId,
                            :terminalId,
                            :sourceSystem,
                            :status,
                            :startedAt,
                            :createdAt
                        )
                        """
                );

        spec = bindNullable(
                spec,
                "correlationId",
                transaction.getCorrelationId(),
                String.class
        );

        spec = bindNullable(
                spec,
                "messageId",
                transaction.getMessageId(),
                String.class
        );

        spec = bindNullable(
                spec,
                "sessionId",
                transaction.getSessionId(),
                String.class
        );

        spec = bindNullable(
                spec,
                "terminalId",
                transaction.getTerminalId(),
                String.class
        );

        spec = bindNullable(
                spec,
                "sourceSystem",
                transaction.getSourceSystem(),
                String.class
        );

        spec = bindNullable(
                spec,
                "status",
                transaction.getStatus(),
                String.class
        );

        spec = bindNullable(
                spec,
                "startedAt",
                transaction.getStartedAt(),
                LocalDateTime.class
        );

        spec = bindNullable(
                spec,
                "createdAt",
                transaction.getCreatedAt(),
                LocalDateTime.class
        );

        return spec
                .filter(
                        statement ->
                                statement.returnGeneratedValues("id")
                )
                .map(
                        (row, metadata) ->
                                row.get("id", Long.class)
                )
                .one();
    }

    public Mono<Void> complete(
            Long transactionId,
            String status,
            LocalDateTime completedAt,
            Long latencyMs) {

        if (transactionId == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "Transaction ID cannot be null"
                    )
            );
        }

        DatabaseClient.GenericExecuteSpec spec =
                databaseClient.sql(
                        """
                        UPDATE topcore_transaction
                        SET
                            status = :status,
                            completed_at = :completedAt,
                            latency_ms = :latencyMs
                        WHERE id = :transactionId
                        """
                );

        spec = bindNullable(
                spec,
                "status",
                status,
                String.class
        );

        spec = bindNullable(
                spec,
                "completedAt",
                completedAt,
                LocalDateTime.class
        );

        spec = bindNullable(
                spec,
                "latencyMs",
                latencyMs,
                Long.class
        );

        spec = spec.bind(
                "transactionId",
                transactionId
        );

        return spec
                .fetch()
                .rowsUpdated()
                .then();
    }

    private <T> DatabaseClient.GenericExecuteSpec bindNullable(
            DatabaseClient.GenericExecuteSpec spec,
            String parameter,
            T value,
            Class<T> type) {

        if (value == null) {
            return spec.bindNull(parameter, type);
        }

        return spec.bind(parameter, value);
    }
}