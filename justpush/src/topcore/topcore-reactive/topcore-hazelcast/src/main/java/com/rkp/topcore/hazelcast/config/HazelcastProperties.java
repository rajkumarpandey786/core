package com.rkp.topcore.hazelcast.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "topcore.hazelcast")
public class HazelcastProperties {

    /**
     * Enables or disables TopCore Hazelcast integration.
     */
    private boolean enabled = true;

    /**
     * Hazelcast cluster name.
     */
    private String clusterName = "dev";

    /**
     * Hazelcast server addresses.
     */
    private List<String> addresses = new ArrayList<>(
            List.of("localhost:5701")
    );

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    @Override
    public String toString() {
        return "HazelcastProperties{" +
                "enabled=" + enabled +
                ", clusterName='" + clusterName + '\'' +
                ", addresses=" + addresses +
                '}';
    }
}
