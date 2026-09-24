package com.rkp.mockp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.rkp.mockp.security.SecurityProperties;

@SpringBootApplication
@EnableConfigurationProperties(SecurityProperties.class)
public class MockpApplication {

	public static void main(String[] args) {
		SpringApplication.run(MockpApplication.class, args);
	}

}
