package com.rkp.topcore.core.database.schema;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        prefix = "topcore.database",
        name = "enabled",
        havingValue = "true"
)
public class DatabaseSchemaInitializer {

    private final DatabaseClient databaseClient;


    public DatabaseSchemaInitializer(
            DatabaseClient databaseClient) {

        if (databaseClient == null) {

            throw new IllegalArgumentException(
                    "DatabaseClient cannot be null"
            );
        }

        this.databaseClient =
                databaseClient;
    }


    @PostConstruct
    public void initialize() {

        execute(
                DatabaseSchema.CREATE_TRANSACTION_TABLE
        );

        execute(
                DatabaseSchema.CREATE_MESSAGE_TABLE
        );

        execute(
                DatabaseSchema.CREATE_TRANSACTION_CORRELATION_INDEX
        );

        execute(
                DatabaseSchema.CREATE_TRANSACTION_MESSAGE_INDEX
        );

        execute(
                DatabaseSchema.CREATE_MESSAGE_TRANSACTION_INDEX
        );

        execute(
                DatabaseSchema.CREATE_MESSAGE_SYSTEM_INDEX
        );
    }


    private void execute(
            String sql) {

        databaseClient
                .sql(sql)
                .fetch()
                .rowsUpdated()
                .block();
    }
}