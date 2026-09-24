package com.rkp.topcore.core.session;

import reactor.netty.Connection;



public class ClientSession {

    private final String sessionId;

    private volatile String terminalId;

    private volatile long lastActivity;

    private volatile Connection connection;


    public ClientSession(
            String sessionId) {

        this.sessionId =
                sessionId;

        /*
         * Session creation timestamp.
         *
         * Note:
         * GatewayHandler should create the session only when
         * the first ECHO or TRANSACTION is received.
         */
        this.lastActivity =
                System.currentTimeMillis();
    }


    public String getSessionId() {

        return sessionId;
    }


    public String getTerminalId() {

        return terminalId;
    }


    public void setTerminalId(
            String terminalId) {

        this.terminalId =
                terminalId;
    }


    /**
     * =========================================================
     * ACTIVITY
     * =========================================================
     *
     * Refresh application session activity.
     *
     * Called for:
     *
     *     ECHO
     *     TRANSACTION
     */
    public void touch() {

        this.lastActivity =
                System.currentTimeMillis();
    }


    public long getLastActivity() {

        return lastActivity;
    }


    /**
     * Returns the amount of time for which the session
     * has received no application activity.
     */
    public long inactivityMillis() {

        return System.currentTimeMillis()
                - lastActivity;
    }


    /**
     * Determines whether the application session has
     * exceeded the configured inactivity timeout.
     */
    public boolean isExpired(
            long timeoutMillis) {

        if (timeoutMillis <= 0) {

            return false;
        }

        return inactivityMillis()
                >= timeoutMillis;
    }


    public Connection getConnection() {

        return connection;
    }


    public void setConnection(
            Connection connection) {

        this.connection =
                connection;
    }
}
