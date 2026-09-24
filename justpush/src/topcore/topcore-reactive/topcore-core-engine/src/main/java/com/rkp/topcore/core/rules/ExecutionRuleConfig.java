package com.rkp.topcore.core.rules;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "execution")
public class ExecutionRuleConfig {

    private List<Rule> rules = new ArrayList<>();

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public static class Rule {

        private String name;

        private int priority = 100;

        private Map<String, List<String>> when =
                new LinkedHashMap<>();

        private RuleResult result = new RuleResult();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public Map<String, List<String>> getWhen() {
            return when;
        }

        public void setWhen(Map<String, List<String>> when) {
            this.when = when;
        }

        public RuleResult getResult() {
            return result;
        }

        public void setResult(RuleResult result) {
            this.result = result;
        }
    }

    public static class RuleResult {

        private String responseCode;

        private String message;

        private String action;

        private String hostCode;

        private String settlement;

        public String getResponseCode() {
            return responseCode;
        }

        public void setResponseCode(String responseCode) {
            this.responseCode = responseCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getHostCode() {
            return hostCode;
        }

        public void setHostCode(String hostCode) {
            this.hostCode = hostCode;
        }

        public String getSettlement() {
            return settlement;
        }

        public void setSettlement(String settlement) {
            this.settlement = settlement;
        }
    }
}