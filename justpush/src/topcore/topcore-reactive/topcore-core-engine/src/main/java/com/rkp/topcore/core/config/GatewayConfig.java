package com.rkp.topcore.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "gateway")
public class GatewayConfig {

    private final Tcp tcp = new Tcp();
    private final Protocol protocol = new Protocol();
    private final Downstream downstream = new Downstream();
    
    private final Upstream upstream = new Upstream();

    public Upstream getUpstream() {
        return upstream;
    }

    public Tcp getTcp() {
        return tcp;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Downstream getDownstream() {
        return downstream;
    }

    /*
     * =========================================================
     * TCP CONFIGURATION
     * =========================================================
     */
    public static class Tcp {

        private String host;
        private int port;
        private boolean keepAlive;
        private boolean tcpNoDelay;
        private int receiveBuffer;
        private int sendBuffer;
        private int maxFrameLength;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public boolean isKeepAlive() {
            return keepAlive;
        }

        public void setKeepAlive(boolean keepAlive) {
            this.keepAlive = keepAlive;
        }

        public boolean isTcpNoDelay() {
            return tcpNoDelay;
        }

        public void setTcpNoDelay(boolean tcpNoDelay) {
            this.tcpNoDelay = tcpNoDelay;
        }

        public int getReceiveBuffer() {
            return receiveBuffer;
        }

        public void setReceiveBuffer(int receiveBuffer) {
            this.receiveBuffer = receiveBuffer;
        }

        public int getSendBuffer() {
            return sendBuffer;
        }

        public void setSendBuffer(int sendBuffer) {
            this.sendBuffer = sendBuffer;
        }

        public int getMaxFrameLength() {
            return maxFrameLength;
        }

        public void setMaxFrameLength(int maxFrameLength) {
            this.maxFrameLength = maxFrameLength;
        }
    }

    public static class Upstream {

        private String integrationId;

        public String getIntegrationId() {
            return integrationId;
        }

        public void setIntegrationId(String integrationId) {

            if (integrationId == null ||
                    integrationId.isBlank()) {

                throw new IllegalArgumentException(
                        "upstream.integrationId cannot be null or blank");
            }

            this.integrationId =
                    integrationId;
        }
    }
    
    public static class Protocol {

        /*
         * Whether ECHO processing is enabled.
         */
        private boolean requireEcho;

        /*
         * Application session inactivity timeout.
         *
         * If no ECHO or TRANSACTION is received during this
         * period, the application session is expired.
         *
         * This is NOT the TCP keep-alive timeout.
         */
        private int sessionTimeoutSeconds;


        public boolean isRequireEcho() {
            return requireEcho;
        }

        public void setRequireEcho(boolean requireEcho) {
            this.requireEcho = requireEcho;
        }


        public int getSessionTimeoutSeconds() {
            return sessionTimeoutSeconds;
        }

        public void setSessionTimeoutSeconds(
                int sessionTimeoutSeconds) {

            if (sessionTimeoutSeconds <= 0) {
                throw new IllegalArgumentException(
                        "sessionTimeoutSeconds must be greater than zero"
                );
            }

            this.sessionTimeoutSeconds =
                    sessionTimeoutSeconds;
        }
    }


    /*
     * =========================================================
     * DOWNSTREAM CONFIGURATION
     * =========================================================
     */
    public static class Downstream {

        private int transactionTimeoutMs;
        private int maxInflightTransactions;

        private List<String> active =
                new ArrayList<>();

        private Map<String, SystemConfig> systems =
                new LinkedHashMap<>();


        public int getTransactionTimeoutMs() {
            return transactionTimeoutMs;
        }

        public void setTransactionTimeoutMs(
                int transactionTimeoutMs) {

            this.transactionTimeoutMs =
                    transactionTimeoutMs;
        }


        public int getMaxInflightTransactions() {
            return maxInflightTransactions;
        }

        public void setMaxInflightTransactions(
                int maxInflightTransactions) {

            this.maxInflightTransactions =
                    maxInflightTransactions;
        }


        public List<String> getActive() {
            return active;
        }

        public void setActive(
                List<String> active) {

            this.active = active;
        }


        public Map<String, SystemConfig> getSystems() {
            return systems;
        }

        public void setSystems(
                Map<String, SystemConfig> systems) {

            this.systems = systems;
        }


        /*
         * =====================================================
         * DOWNSTREAM SYSTEM CONFIGURATION
         * =====================================================
         */
        public static class SystemConfig {

            private String url;


            /*
             * HTTP connection settings
             */
            private int connectTimeoutMs = 500;
            private int responseTimeoutMs = 1000;


            /*
             * Connection pool settings
             */
            private int maxConnections = 500;
            private int pendingAcquireMaxCount = 10000;
            private long pendingAcquireTimeoutMs = 30000;


            /*
             * Connection lifecycle settings
             */
            private long maxIdleTimeMs = 60000;
            private long maxLifeTimeMs = 600000;

            private boolean keepAlive = true;

            private Authentication authentication =
                    new Authentication();

            public Authentication getAuthentication() {
                return authentication;
            }

            public void setAuthentication(
                    Authentication authentication) {

                this.authentication =
                        authentication;
            }
            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }


            public int getConnectTimeoutMs() {
                return connectTimeoutMs;
            }

            public void setConnectTimeoutMs(
                    int connectTimeoutMs) {

                this.connectTimeoutMs =
                        connectTimeoutMs;
            }


            public int getResponseTimeoutMs() {
                return responseTimeoutMs;
            }

            public void setResponseTimeoutMs(
                    int responseTimeoutMs) {

                this.responseTimeoutMs =
                        responseTimeoutMs;
            }


            public int getMaxConnections() {
                return maxConnections;
            }

            public void setMaxConnections(
                    int maxConnections) {

                this.maxConnections =
                        maxConnections;
            }


            public int getPendingAcquireMaxCount() {
                return pendingAcquireMaxCount;
            }

            public void setPendingAcquireMaxCount(
                    int pendingAcquireMaxCount) {

                this.pendingAcquireMaxCount =
                        pendingAcquireMaxCount;
            }


            public long getPendingAcquireTimeoutMs() {
                return pendingAcquireTimeoutMs;
            }

            public void setPendingAcquireTimeoutMs(
                    long pendingAcquireTimeoutMs) {

                this.pendingAcquireTimeoutMs =
                        pendingAcquireTimeoutMs;
            }


            public long getMaxIdleTimeMs() {
                return maxIdleTimeMs;
            }

            public void setMaxIdleTimeMs(
                    long maxIdleTimeMs) {

                this.maxIdleTimeMs =
                        maxIdleTimeMs;
            }


            public long getMaxLifeTimeMs() {
                return maxLifeTimeMs;
            }

            public void setMaxLifeTimeMs(
                    long maxLifeTimeMs) {

                this.maxLifeTimeMs =
                        maxLifeTimeMs;
            }


            public boolean isKeepAlive() {
                return keepAlive;
            }

            public void setKeepAlive(
                    boolean keepAlive) {

                this.keepAlive = keepAlive;
            }
            
            public static class Authentication {

                private String tokenUrl;
                private String clientId;
                private String clientSecret;
                private String scope;
                private long refreshSkewSeconds = 60;

                public String getTokenUrl() {
                    return tokenUrl;
                }

                public void setTokenUrl(
                        String tokenUrl) {
                    this.tokenUrl = tokenUrl;
                }

                public String getClientId() {
                    return clientId;
                }

                public void setClientId(
                        String clientId) {
                    this.clientId = clientId;
                }

                public String getClientSecret() {
                    return clientSecret;
                }

                public void setClientSecret(
                        String clientSecret) {
                    this.clientSecret = clientSecret;
                }

                public String getScope() {
                    return scope;
                }

                public void setScope(
                        String scope) {
                    this.scope = scope;
                }

                public long getRefreshSkewSeconds() {
                    return refreshSkewSeconds;
                }

                public void setRefreshSkewSeconds(
                        long refreshSkewSeconds) {
                    this.refreshSkewSeconds =
                            refreshSkewSeconds;
                }
            }
        }
        
       
    }
}
