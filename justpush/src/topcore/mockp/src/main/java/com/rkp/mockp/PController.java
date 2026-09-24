package com.rkp.mockp;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@RestController
public class PController {
	long counter =1L;
    @PostMapping(
            value = "/api/process",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> process(@RequestBody String request) {
    	System.out.println("request->"+request);
        ObjectMapper objectMapper = new ObjectMapper();

        return Mono.fromCallable(() -> objectMapper.readTree(request))
                .map(rootNode -> rootNode.path("uuMid").asText("-"))
                .flatMap(txnno ->
                        Mono.delay(Duration.ofMillis(500))
                                .map(i -> buildResponse(txnno))
                );
        
    }

    private String buildResponse(String txnno) {

        String response= "{"
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
                + "\"transactionReferenceNumber\": \"" + txnno + "\""
                + "}";
        System.out.println("response->"+response); 
        if(counter % 500 !=0)
        {
        	counter++;
        	System.out.print(".");
        }
        else
        {
        	counter++;
        	System.out.println(".");
        }
        
        return response;
    }
}