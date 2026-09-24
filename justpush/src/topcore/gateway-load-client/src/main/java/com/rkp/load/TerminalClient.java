package com.rkp.load;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

public class TerminalClient {

    private final String host;
    private final int port;
    private final String terminalId;
    private final int tps;
    private final MetricsCollector metrics;
    private final TerminalSecurityConfig securityConfig;

    private final Map<String, Long> inflight =
            new ConcurrentHashMap<>();

    private Socket socket;
    private InputStream in;
    private OutputStream out;

    private long counter;

    public TerminalClient(String host,
                          int port,
                          String terminalId,
                          int tps,
                          MetricsCollector metrics,
                          TerminalSecurityConfig securityConfig) {

        this.host = host;
        this.port = port;
        this.terminalId = terminalId;
        this.tps = tps;
        this.metrics = metrics;
        this.securityConfig = securityConfig;
    }

    public void start() {

        try {

            /*
             * ---------------------------------------------------------
             * CREATE SOCKET
             * ---------------------------------------------------------
             *
             * mTLS enabled:
             *      SSLSocket
             *
             * mTLS disabled:
             *      normal TCP Socket
             */
            socket = createSocket();

            /*
             * TCP keepalive is still useful at the socket level.
             *
             * This is independent of TLS.
             */
            socket.setKeepAlive(true);

            /*
             * Establish TLS handshake before any application message
             * is sent.
             */
            if (socket instanceof SSLSocket sslSocket) {

            	sslSocket.setSoTimeout(
            	        Math.toIntExact(
            	                securityConfig.getHandshakeTimeoutMs()));

                sslSocket.startHandshake();

                /*
                 * Reset read timeout after TLS handshake.
                 *
                 * We don't want the handshake timeout to become the
                 * normal application read timeout.
                 */
                sslSocket.setSoTimeout(0);
            }

            in = socket.getInputStream();

            out = socket.getOutputStream();

            /*
             * ---------------------------------------------------------
             * APPLICATION SESSION ESTABLISHMENT
             * ---------------------------------------------------------
             */

          //  signon();

          //  keyExchange();
            echo();

            startReceiver();
            Thread.sleep(100);
            startSender();

        } catch (Exception e) {

            metrics.error();

            throw new RuntimeException(
                    "Unable to start terminal " +
                    terminalId +
                    " connection to " +
                    host +
                    ":" +
                    port,
                    e);
        }
    }

    /**
     * Creates either a normal TCP socket or an mTLS SSL socket.
     */
    private Socket createSocket() throws Exception {

        if (!securityConfig.isMtlsEnabled()) {

            System.out.println(
                    terminalId +
                    " connecting using plain TCP " +
                    host +
                    ":" +
                    port);

            return new Socket(host, port);
        }

        System.out.println(
                terminalId +
                " connecting using mTLS " +
                host +
                ":" +
                port);

        SSLContext sslContext =
                createSslContext();

        /*
         * Create SSL socket directly to the gateway.
         */
        SSLSocket sslSocket =
                (SSLSocket) sslContext
                        .getSocketFactory()
                        .createSocket(host, port);

        /*
         * Restrict TLS versions to the configured protocol.
         */
        sslSocket.setEnabledProtocols(
                new String[]{
                        securityConfig.getProtocol()
                });

        return sslSocket;
    }

    /**
     * Creates the client-side SSLContext.
     *
     * KeyStore:
     *      Contains terminal-client private key + certificate.
     *
     * TrustStore:
     *      Contains test-root-ca used to validate the gateway
     *      certificate.
     */
    private SSLContext createSslContext()
            throws Exception {

        TerminalSecurityConfig.KeyStoreConfig keyCfg =
                securityConfig.getKeyStore();

        TerminalSecurityConfig.TrustStoreConfig trustCfg =
                securityConfig.getTrustStore();

        /*
         * ---------------------------------------------------------
         * CLIENT KEYSTORE
         * ---------------------------------------------------------
         *
         * terminal-client.p12
         *
         * Contains:
         *
         * terminal-client -> PrivateKeyEntry
         */
        KeyStore keyStore =
                KeyStore.getInstance(keyCfg.getType());

        try (InputStream input =
                     new java.io.FileInputStream(
                             keyCfg.getPath())) {

            keyStore.load(
                    input,
                    keyCfg.getPassword().toCharArray());
        }

        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(
                        KeyManagerFactory.getDefaultAlgorithm());

        keyManagerFactory.init(
                keyStore,
                keyCfg.getPassword().toCharArray());

        /*
         * ---------------------------------------------------------
         * TRUSTSTORE
         * ---------------------------------------------------------
         *
         * terminal-client-truststore.p12
         *
         * Contains:
         *
         * test-root-ca -> trustedCertEntry
         */
        KeyStore trustStore =
                KeyStore.getInstance(trustCfg.getType());

        try (InputStream input =
                     new java.io.FileInputStream(
                             trustCfg.getPath())) {

            trustStore.load(
                    input,
                    trustCfg.getPassword().toCharArray());
        }

        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm());

        trustManagerFactory.init(trustStore);

        /*
         * ---------------------------------------------------------
         * SSL CONTEXT
         * ---------------------------------------------------------
         */
        SSLContext sslContext =
                SSLContext.getInstance(
                        securityConfig.getProtocol());

        sslContext.init(
                keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(),
                null);

        return sslContext;
    }

    private void signon() throws Exception {

        sendSync(
                "<root>" +
                "<rec>SIGNON</rec>" +
                "<msgId>S-" + terminalId + "</msgId>" +
                "<terminalId>" + terminalId + "</terminalId>" +
                "</root>");
    }

    private void keyExchange() throws Exception {

        sendSync(
                "<root>" +
                "<rec>KEY_EXCHANGE</rec>" +
                "<msgId>K-" + terminalId + "</msgId>" +
                "<terminalId>" + terminalId + "</terminalId>" +
                "</root>");
    }
    private void echo() throws Exception
    {
    	sendSync("<root><rec><Echo>ECHO</Echo></rec></root>");
    }

    private void startSender() {

        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        long interval =
                1000L / tps;

        scheduler.scheduleAtFixedRate(() -> {

            try {

                
                        

                String xml ="<root><rec><Country>GH</Country><ReqID>notifyTSAS</ReqID><FunctionName>CPHTSAAS</FunctionName><externalTransactionId>TSA288"+System.currentTimeMillis()+"</externalTransactionId><Chnl>TSAS</Chnl><type>DOM</type><status>Approve</status><MTI>0100</MTI><CardNbr>486056******8643</CardNbr><ProcessingCode>260000</ProcessingCode><TrxnAmt>0000000006.00</TrxnAmt><BillingAmt>0000000006.00</BillingAmt><TransmissionDateTime>0502162109</TransmissionDateTime><SysTrace>287796</SysTrace><LocalTrxnTime>000000</LocalTrxnTime><LocalTrxnDate>0000</LocalTrxnDate><MCC>4829</MCC><AcqCountryCode>288</AcqCountryCode><POSEntryMode>010</POSEntryMode><CardSeqNbr>0</CardSeqNbr><POSConditionCode>59</POSConditionCode><AcqInstID>416080</AcqInstID><RetrievalRefNbr>312216287796</RetrievalRefNbr><TerminalID>EXPAYGH</TerminalID><MerchantID>EXPAYGH</MerchantID><MerchantName>exPay Accra GH</MerchantName><TrxnCatCode></TrxnCatCode><OBService></OBService><OBSResult1></OBSResult1><ScreeningScore></ScreeningScore><WLMResultsCode></WLMResultsCode><TrxnCurrCode>936</TrxnCurrCode><BillingCurrCode>936</BillingCurrCode><SchemeTrxnID>583122588698861</SchemeTrxnID><TrxnType>PP</TrxnType><UniqueTrxnRef></UniqueTrxnRef><SenderAcctNbr>312216287796</SenderAcctNbr><SenderName></SenderName><SenderAddr></SenderAddr><SenderCity></SenderCity><SenderState></SenderState><SenderCountry>GHA</SenderCountry><FundingSource>02</FundingSource><SenderPostalCode></SenderPostalCode><SenderPhoneNbr></SenderPhoneNbr><SenderDOB></SenderDOB><SenderIDType></SenderIDType><SenderID></SenderID><SenderIDCountryCode></SenderIDCountryCode><SenderIDExpDate></SenderIDExpDate><SenderNationality></SenderNationality><SenderCountryOfBirth></SenderCountryOfBirth><RecipientName>Joseph Ampah</RecipientName><RecipientAddr></RecipientAddr><RecipientCity></RecipientCity><RecipientState></RecipientState><RecipientCountry>GHA</RecipientCountry><RecipientPostalCode></RecipientPostalCode><RecipientPhoneNbr></RecipientPhoneNbr><RecipientDOB></RecipientDOB><RecipientAcctNbr></RecipientAcctNbr><RecipientIDType></RecipientIDType><RecipientID></RecipientID><RecipientIDCountryCode></RecipientIDCountryCode><RecipientIDExpDate></RecipientIDExpDate><RecipientNationality></RecipientNationality><RecipientCountryOfBirth></RecipientCountryOfBirth><AdditionalMsg></AdditionalMsg><ParticipationID></ParticipationID><TrxnPurpose></TrxnPurpose><LanguageID></LanguageID><LanguageData></LanguageData><AcceptorLegalBusinessName>UN</AcceptorLegalBusinessName><PaymentFacilitatorName>UN</PaymentFacilitatorName></rec></root>";
                System.out.println("request->"+xml);
                String msgId = extract(xml);
                inflight.put(
                        msgId,
                        System.currentTimeMillis());

                send(xml);

                metrics.sent();

            } catch (Exception e) {
            	e.printStackTrace();        
            	metrics.error();

                System.err.println(
                        terminalId +
                        " send error: " +
                        e.getMessage());
            }

        }, 0, interval, TimeUnit.MILLISECONDS);
    }

    private void startReceiver() {

        Thread receiver =
                new Thread(() -> {

                    try {

                        while (true) {

                            int b1 = in.read();

                            int b2 = in.read();

                            if (b1 < 0 || b2 < 0) {

                                break;
                            }

                            int length =
                                    ((b1 & 0xFF) << 8)
                                    | (b2 & 0xFF);

                            byte[] data =
                                    in.readNBytes(length);

                            if (data.length != length) {

                                throw new java.io.EOFException(
                                        "Expected " +
                                        length +
                                        " bytes but received " +
                                        data.length);
                            }

                            String xml =
                                    new String(
                                            data,
                                            StandardCharsets.UTF_8);
                            System.out.println("Response-xml->"+xml);
                            String msgId = extract(xml);
                            //System.out.println("inflight->"+inflight);

                            Long start =
                                    inflight.remove(msgId);

                            if (start == null) {

                                System.err.println(
                                        terminalId +
                                        " MISMATCH: " +
                                        msgId);

                                metrics.error();

                            } else {

                                metrics.received(
                                        System.currentTimeMillis()
                                        - start);
                            }
                        }

                    } catch (Exception e) {

                        metrics.error();

                        System.err.println(
                                terminalId +
                                " receiver error: " +
                                e);
                    }

                });

        receiver.setName(
                "terminal-" + terminalId + "-receiver");

        receiver.setDaemon(true);

        receiver.start();
    }

    private String extract(String xml) {

        int s =
                xml.indexOf("<externalTransactionId>");

        int e =
                xml.indexOf("</externalTransactionId>");

        if (s < 0 || e < 0) {

            return "";
        }

        return xml.substring(
                s + 23,
                e);
    }

    private void sendSync(String xml)
            throws Exception {
    	System.out.println("request->"+xml);
        send(xml);

        int b1 = in.read();

        int b2 = in.read();

        if (b1 < 0 || b2 < 0) {

            throw new java.io.EOFException(
                    "Connection closed while waiting for response");
        }

        int len =
                ((b1 & 0xFF) << 8)
                | (b2 & 0xFF);

        byte[] response =
                in.readNBytes(len);

        if (response.length != len) {

            throw new java.io.EOFException(
                    "Expected " +
                    len +
                    " response bytes but received " +
                    response.length);
        }
        System.out.println("response->"+new String(response));
    }
    
    private synchronized void send(String xml)
            throws Exception {

        byte[] bytes =
                xml.getBytes(StandardCharsets.UTF_8);

        if (bytes.length > 65535) {

            throw new IllegalArgumentException(
                    "Message too large: " + bytes.length
            );
        }

       /* System.out.println(
                terminalId +
                " TX length=" +
                bytes.length
        );

        System.out.println(
                terminalId +
                " TX XML=" +
                xml
        );
*/
        out.write(
                (bytes.length >> 8) & 0xFF
        );

        out.write(
                bytes.length & 0xFF
        );

        out.write(bytes);

        out.flush();
    }

    private synchronized void send1(
            String xml) throws Exception {

        byte[] bytes =
                xml.getBytes(
                        StandardCharsets.UTF_8);

        if (bytes.length > 65535) {

            throw new IllegalArgumentException(
                    "Message too large: " +
                    bytes.length);
        }

        /*
         * Gateway uses LengthFieldPrepender(2),
         * therefore client sends a 2-byte big-endian
         * length prefix.
         */
        out.write(
                (bytes.length >> 8) & 0xFF);

        out.write(
                bytes.length & 0xFF);

        out.write(bytes);

        out.flush();
    }
}