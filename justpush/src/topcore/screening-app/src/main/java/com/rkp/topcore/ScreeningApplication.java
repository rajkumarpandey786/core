package com.rkp.topcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import com.rkp.topcore.core.config.GatewayConfig;
import com.rkp.topcore.core.rules.ExecutionRuleConfig;
import com.rkp.topcore.core.security.SecurityProperties;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.rkp.topcore",
        "com.rkp.topcore.canonical",
        "com.rkp.topcore.database"
})
@EnableConfigurationProperties({
        GatewayConfig.class,
        SecurityProperties.class,
        ExecutionRuleConfig.class
})
public class ScreeningApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                ScreeningApplication.class,
                args);
    }
}