package com.rkp.topcore.core.database.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "topcore.database")
public class DatabaseProperties {

    private boolean enabled;

    private String driver;

    private String mode;

    private R2dbcProperties r2dbc =
            new R2dbcProperties();

    private PoolProperties pool =
            new PoolProperties();

    private PersistenceProperties persistence =
            new PersistenceProperties();


    public boolean isEnabled() {

        return enabled;
    }

    public void setEnabled(
            boolean enabled) {

        this.enabled = enabled;
    }


    public String getDriver() {

        return driver;
    }

    public void setDriver(
            String driver) {

        this.driver = driver;
    }


    public String getMode() {

        return mode;
    }

    public void setMode(
            String mode) {

        this.mode = mode;
    }


    public R2dbcProperties getR2dbc() {

        return r2dbc;
    }

    public void setR2dbc(
            R2dbcProperties r2dbc) {

        this.r2dbc =
                r2dbc == null
                        ? new R2dbcProperties()
                        : r2dbc;
    }


    public PoolProperties getPool() {

        return pool;
    }

    public void setPool(
            PoolProperties pool) {

        this.pool =
                pool == null
                        ? new PoolProperties()
                        : pool;
    }


    public PersistenceProperties getPersistence() {

        return persistence;
    }

    public void setPersistence(
            PersistenceProperties persistence) {

        this.persistence =
                persistence == null
                        ? new PersistenceProperties()
                        : persistence;
    }


    public static class R2dbcProperties {

        private String url;

        private String username;

        private String password;


        public String getUrl() {

            return url;
        }

        public void setUrl(
                String url) {

            this.url = url;
        }


        public String getUsername() {

            return username;
        }

        public void setUsername(
                String username) {

            this.username = username;
        }


        public String getPassword() {

            return password;
        }

        public void setPassword(
                String password) {

            this.password = password;
        }
    }


    public static class PoolProperties {

        private boolean enabled = true;

        private int initialSize = 5;

        private int maxSize = 20;

        private long maxIdleTimeSeconds = 300;


        public boolean isEnabled() {

            return enabled;
        }

        public void setEnabled(
                boolean enabled) {

            this.enabled = enabled;
        }


        public int getInitialSize() {

            return initialSize;
        }

        public void setInitialSize(
                int initialSize) {

            this.initialSize = initialSize;
        }


        public int getMaxSize() {

            return maxSize;
        }

        public void setMaxSize(
                int maxSize) {

            this.maxSize = maxSize;
        }


        public long getMaxIdleTimeSeconds() {

            return maxIdleTimeSeconds;
        }

        public void setMaxIdleTimeSeconds(
                long maxIdleTimeSeconds) {

            this.maxIdleTimeSeconds =
                    maxIdleTimeSeconds;
        }


        public Duration getMaxIdleTime() {

            return Duration.ofSeconds(
                    maxIdleTimeSeconds
            );
        }
    }


    public static class PersistenceProperties {

        private TransactionProperties transaction =
                new TransactionProperties();

        private MessageProperties message =
                new MessageProperties();


        public TransactionProperties getTransaction() {

            return transaction;
        }

        public void setTransaction(
                TransactionProperties transaction) {

            this.transaction =
                    transaction == null
                            ? new TransactionProperties()
                            : transaction;
        }


        public MessageProperties getMessage() {

            return message;
        }

        public void setMessage(
                MessageProperties message) {

            this.message =
                    message == null
                            ? new MessageProperties()
                            : message;
        }
    }


    public static class TransactionProperties {

        private boolean enabled = true;


        public boolean isEnabled() {

            return enabled;
        }

        public void setEnabled(
                boolean enabled) {

            this.enabled = enabled;
        }
    }


    public static class MessageProperties {

        private boolean enabled = true;


        public boolean isEnabled() {

            return enabled;
        }

        public void setEnabled(
                boolean enabled) {

            this.enabled = enabled;
        }
    }
}