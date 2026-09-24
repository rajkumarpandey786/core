package com.rkp.mockt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.rkp.mockt.security.SecurityProperties;

@SpringBootApplication
@EnableConfigurationProperties(SecurityProperties.class)
public class MocktApplication {

	public static void main(String[] args) {
		SpringApplication.run(MocktApplication.class, args);
	}

}
