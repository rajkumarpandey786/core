package com.rkp.mockt;

import java.time.Duration;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@RestController
public class TController {

    private long counter = 1L;
//UV7m0vpKNuxMExpMyGNySoRCfJUa:5iT0XnSWg2RiXWhl7lfIfNgUhBwa
    private static final String CLIENT_ID =
            "UV7m0vpKNuxMExpMyGNySoRCfJUa";

    private static final String CLIENT_SECRET =
            "5iT0XnSWg2RiXWhl7lfIfNgUhBwa";

    private static final String SCOPE =
            "TSaaSScreeningAPIService_POST";

    private static final long TOKEN_EXPIRY_SECONDS =
            300;

    private volatile String currentAccessToken;

    private volatile long tokenExpiresAt;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @PostMapping(
            value = "/functions/api/v1/txnscreening/oauth2/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> token(
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization,
            org.springframework.web.server.ServerWebExchange exchange) {

        validateBasicAuthorization(authorization);

        return exchange.getFormData()
                .flatMap(form -> {

                    String scope =
                            form.getFirst("scope");

                    String grantType =
                            form.getFirst("grant_type");

                    if (!SCOPE.equals(scope)) {
                        return Mono.error(
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Invalid scope"));
                    }

                    if (!"client_credentials".equals(grantType)) {
                        return Mono.error(
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Invalid grant_type"));
                    }

                    String accessToken =
                            UUID.randomUUID().toString();

                    long expiresAt =
                            System.currentTimeMillis()
                                    + (TOKEN_EXPIRY_SECONDS * 1000L);

                    currentAccessToken =
                            accessToken;

                    tokenExpiresAt =
                            expiresAt;

                    String response =
                            "{"
                                    + "\"access_token\":\""
                                    + accessToken
                                    + "\","
                                    + "\"token_type\":\"Bearer\","
                                    + "\"expires_in\":"
                                    + TOKEN_EXPIRY_SECONDS
                                    + ","
                                    + "\"scope\":\""
                                    + SCOPE
                                    + "\""
                                    + "}";

                    System.out.println(
                            "OAuth token generated");

                    return Mono.just(response);
                });
    }

    @PostMapping(
            value = "/functions/api/v1/txnscreening/process",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> process(
            @RequestHeader(
                    value = HttpHeaders.AUTHORIZATION,
                    required = false)
            String authorization,
            @RequestBody String request) {

        validateBearerToken(
                authorization);

        System.out.println(
                "request->" + request);

        return Mono.fromCallable(
                () -> objectMapper.readTree(request))
                .map(rootNode ->
                        rootNode.path("uuMid")
                                .asText("-"))
                .flatMap(txnno ->
                        Mono.delay(
                                Duration.ofMillis(500))
                                .map(i ->
                                        buildResponse(txnno)));
    }

    private void validateBasicAuthorization(
            String authorization) {

        if (authorization == null ||
                !authorization.startsWith("Basic ")) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Missing Basic Authorization");
        }

        String encoded =
                authorization.substring(6);

        String expected =
                java.util.Base64.getEncoder()
                        .encodeToString(
                                (CLIENT_ID + ":" + CLIENT_SECRET)
                                        .getBytes(
                                                java.nio.charset.StandardCharsets
                                                        .UTF_8));

        if (!expected.equals(encoded)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid client credentials");
        }
    }

    private void validateBearerToken(
            String authorization) {

        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Missing Bearer token");
        }

        String token =
                authorization.substring(7);

        if (currentAccessToken == null ||
                !currentAccessToken.equals(token)) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid access token");
        }

        if (System.currentTimeMillis()
                >= tokenExpiresAt) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Access token expired");
        }
    }

    private String buildResponse(
            String txnno) {

        String response =
                "{"
                        + "\"callbackUrl\": \"https://PAIMI\","
                        + "\"messageChecksum\": \"\","
                        + "\"messageStatus\": \"001\","
                        + "\"responseCode\": \"200\","
                        + "\"responseDescription\": \"Ack Sent Successfully\","
                        + "\"screeningRequestDate\": \"04-Mar-26 05:39:34 AM\","
                        + "\"statusComment\": \"Stop: 1, Nonblocking: 0\","
                        + "\"statusLabel\": \"HIT\","
                        + "\"statusOwner\": null,"
                        + "\"systemID\": \"SBCI20260304053934-00000-1151356\","
                        + "\"transactionReferenceNumber\": \""
                        + txnno
                        + "\""
                        + "}";

        System.out.println(
                "response->" + response);

        if (counter % 500 != 0) {
            counter++;
            System.out.print(".");
        } else {
            counter++;
            System.out.println(".");
        }

        return response;
    }
}