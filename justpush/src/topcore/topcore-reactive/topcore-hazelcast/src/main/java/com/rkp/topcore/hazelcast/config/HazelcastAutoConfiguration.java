package com.rkp.topcore.hazelcast.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

@AutoConfiguration
@ConditionalOnClass(HazelcastInstance.class)
@ConditionalOnProperty(
        prefix = "topcore.hazelcast",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(HazelcastProperties.class)
public class HazelcastAutoConfiguration {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public HazelcastInstance hazelcastInstance(
            HazelcastProperties properties) {

        ClientConfig clientConfig = new ClientConfig();

        clientConfig.setClusterName(
                properties.getClusterName()
        );

        clientConfig.getNetworkConfig()
                .addAddress(
                        properties.getAddresses()
                                .toArray(new String[0])
                );

        return HazelcastClient
                .newHazelcastClient(clientConfig);
    }
}
