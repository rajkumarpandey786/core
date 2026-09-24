package com.rkp.topcore.core.database.repository;

import java.time.LocalDateTime;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.rkp.topcore.core.database.model.MessageRecord;

import reactor.core.publisher.Mono;

@Repository
public class MessageRepository {

    private final DatabaseClient databaseClient;

    public MessageRepository(DatabaseClient databaseClient) {

        if (databaseClient == null) {
            throw new IllegalArgumentException(
                    "DatabaseClient cannot be null"
            );
        }

        this.databaseClient = databaseClient;
    }

    public Mono<Void> save(MessageRecord message) {

        if (message == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "MessageRecord cannot be null"
                    )
            );
        }

        DatabaseClient.GenericExecuteSpec spec =
                databaseClient.sql(
                        """
                        INSERT INTO topcore_message (
                            transaction_id,
                            system_name,
                            direction,
                            message_type,
                            payload,
                            masked_payload,
                            status,
                            latency_ms,
                            created_at
                        )
                        VALUES (
                            :transactionId,
                            :systemName,
                            :direction,
                            :messageType,
                            :payload,
                            :maskedPayload,
                            :status,
                            :latencyMs,
                            :createdAt
                        )
                        """
                );

        spec = spec.bind(
                "transactionId",
                message.getTransactionId()
        );

        spec = bindNullable(
                spec,
                "systemName",
                message.getSystemName(),
                String.class
        );

        spec = bindNullable(
                spec,
                "direction",
                message.getDirection(),
                String.class
        );

        spec = bindNullable(
                spec,
                "messageType",
                message.getMessageType(),
                String.class
        );

        spec = bindNullable(
                spec,
                "payload",
                message.getPayload(),
                String.class
        );

        spec = bindNullable(
                spec,
                "maskedPayload",
                message.getMaskedPayload(),
                String.class
        );

        spec = bindNullable(
                spec,
                "status",
                message.getStatus(),
                String.class
        );

        spec = bindNullable(
                spec,
                "latencyMs",
                message.getLatencyMs(),
                Long.class
        );

        spec = bindNullable(
                spec,
                "createdAt",
                message.getCreatedAt(),
                LocalDateTime.class
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