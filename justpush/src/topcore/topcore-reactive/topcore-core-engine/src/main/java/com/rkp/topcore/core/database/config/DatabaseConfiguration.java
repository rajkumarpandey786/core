package com.rkp.topcore.core.database.config;

import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;

@Configuration
@EnableConfigurationProperties(DatabaseProperties.class)
@ConditionalOnProperty(
        prefix = "topcore.database",
        name = "enabled",
        havingValue = "true"
)
public class DatabaseConfiguration {

    private final DatabaseProperties databaseProperties;


    public DatabaseConfiguration(
            DatabaseProperties databaseProperties) {

        if (databaseProperties == null) {

            throw new IllegalArgumentException(
                    "DatabaseProperties cannot be null"
            );
        }

        this.databaseProperties =
                databaseProperties;
    }


    @Bean
    public ConnectionFactory connectionFactory() {

        String url =
                databaseProperties
                        .getR2dbc()
                        .getUrl();

        if (url == null ||
                url.isBlank()) {

            throw new IllegalStateException(
                    "R2DBC database URL cannot be null or blank"
            );
        }

        ConnectionFactoryOptions.Builder optionsBuilder =
                ConnectionFactoryOptions
                        .parse(url)
                        .mutate();


        String username =
                databaseProperties
                        .getR2dbc()
                        .getUsername();

        if (username != null &&
                !username.isBlank()) {

            optionsBuilder.option(
                    USER,
                    username
            );
        }


        String password =
                databaseProperties
                        .getR2dbc()
                        .getPassword();

        if (password != null) {

            optionsBuilder.option(
                    PASSWORD,
                    password
            );
        }


        ConnectionFactory connectionFactory =
                ConnectionFactories.get(
                        optionsBuilder.build()
                );


        if (!databaseProperties
                .getPool()
                .isEnabled()) {

            return connectionFactory;
        }


        ConnectionPoolConfiguration poolConfiguration =
                ConnectionPoolConfiguration
                        .builder(connectionFactory)
                        .initialSize(
                                databaseProperties
                                        .getPool()
                                        .getInitialSize()
                        )
                        .maxSize(
                                databaseProperties
                                        .getPool()
                                        .getMaxSize()
                        )
                        .maxIdleTime(
                                Duration.ofSeconds(
                                        databaseProperties
                                                .getPool()
                                                .getMaxIdleTimeSeconds()
                                )
                        )
                        .build();


        return new ConnectionPool(
                poolConfiguration
        );
    }
}