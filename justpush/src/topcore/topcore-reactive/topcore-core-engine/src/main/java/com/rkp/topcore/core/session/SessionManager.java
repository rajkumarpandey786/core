package com.rkp.topcore.core.session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.rkp.topcore.core.config.GatewayConfig;
import com.rkp.topcore.core.observability.GatewayLogger;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;



@Component
public class SessionManager {

    private static final Logger log =
            GatewayLogger.getLogger(
                    SessionManager.class);


    private final ConcurrentHashMap<
            String,
            ClientSession>
            sessions =
            new ConcurrentHashMap<>();


    private final AtomicLong sessionCounter =
            new AtomicLong();


    private final GatewayConfig config;


    private final ScheduledExecutorService
            expiryExecutor =
            Executors.newSingleThreadScheduledExecutor(
                    runnable -> {

                        Thread thread =
                                new Thread(
                                        runnable,
                                        "session-expiry"
                                );

                        thread.setDaemon(true);

                        return thread;
                    }
            );


    public SessionManager(
            GatewayConfig config) {

        if (config == null) {

            throw new IllegalArgumentException(
                    "GatewayConfig cannot be null"
            );
        }

        this.config =
                config;
    }


    @PostConstruct
    public void startExpiryMonitor() {

        /*
         * Run once every second.
         *
         * The actual expiration decision uses the configured
         * sessionTimeoutSeconds.
         */
        expiryExecutor.scheduleAtFixedRate(
                this::expireInactiveSessions,
                1,
                1,
                TimeUnit.SECONDS
        );


        GatewayLogger.info(
                log,
                "B-200 session expiry monitor started "
                        + "timeout={}s",
                config.getProtocol()
                        .getSessionTimeoutSeconds()
        );
    }


    /**
     * Generate a unique application session ID.
     */
    public String nextSessionId() {

        String sessionId =
                String.format(
                        "S%012d",
                        sessionCounter.incrementAndGet()
                );


        GatewayLogger.debug(
                log,
                "Generated session id {}",
                sessionId
        );


        return sessionId;
    }


    /**
     * Create an application session.
     *
     * This should only be called after a valid ECHO
     * or TRANSACTION has been identified.
     */
    public ClientSession create(
            String sessionId) {

        ClientSession existing =
                sessions.get(sessionId);


        if (existing != null) {

            return existing;
        }


        ClientSession session =
                new ClientSession(
                        sessionId
                );


        ClientSession previous =
                sessions.putIfAbsent(
                        sessionId,
                        session
                );


        if (previous != null) {

            return previous;
        }


        GatewayLogger.info(
                log,
                "Application session created "
                        + "session={} activeSessions={}",
                sessionId,
                sessions.size()
        );


        return session;
    }


    public ClientSession get(
            String sessionId) {

        return sessions.get(
                sessionId
        );
    }


    public void remove(
            String sessionId) {

        ClientSession removed =
                sessions.remove(
                        sessionId
                );


        if (removed != null) {

            GatewayLogger.info(
                    log,
                    "Session removed {} activeSessions={}",
                    sessionId,
                    sessions.size()
            );
        }
    }


    /**
     * =========================================================
     * EXPIRE INACTIVE SESSIONS
     * =========================================================
     */
    private void expireInactiveSessions() {

        long timeoutMillis =
                config.getProtocol()
                        .getSessionTimeoutSeconds()
                        * 1000L;


        for (ClientSession session :
                sessions.values()) {


            if (!session.isExpired(
                    timeoutMillis)) {

                continue;
            }


            String sessionId =
                    session.getSessionId();


            /*
             * Remove the session atomically.
             */
            if (!sessions.remove(
                    sessionId,
                    session)) {

                continue;
            }


            GatewayLogger.info(
                    log,
                    "B-200 session expired "
                            + "due to inactivity "
                            + "session={} terminal={} "
                            + "inactiveMs={}",
                    sessionId,
                    session.getTerminalId(),
                    session.inactivityMillis()
            );


            /*
             * Close the associated TCP connection.
             */
            if (session.getConnection() != null) {

                try {

                    session.getConnection()
                            .dispose();

                } catch (Exception e) {

                    GatewayLogger.warn(
                            log,
                            "Unable to close TCP connection "
                                    + "for expired session={}",
                            sessionId
                    );
                }
            }
        }
    }


    @PreDestroy
    public void shutdown() {

        expiryExecutor.shutdownNow();
    }


    public boolean exists(
            String sessionId) {

        return sessions.containsKey(
                sessionId
        );
    }


    public int size() {

        return sessions.size();
    }


    public long getSessionCounter() {

        return sessionCounter.get();
    }
}
