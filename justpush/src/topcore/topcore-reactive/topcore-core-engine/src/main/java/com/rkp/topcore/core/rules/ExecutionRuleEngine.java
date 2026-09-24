package com.rkp.topcore.core.rules;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.rkp.topcore.core.observability.GatewayLogger;
import com.rkp.topcore.core.orchestrator.AggregatedResult;

@Component
public class ExecutionRuleEngine {

    private static final Logger log =
            GatewayLogger.getLogger(
                    ExecutionRuleEngine.class);

    private final List<ExecutionRuleConfig.Rule> rules;

    public ExecutionRuleEngine(
            ExecutionRuleConfig config) {

        this.rules =
                new ArrayList<>(
                        config.getRules());

        this.rules.sort(
                Comparator.comparingInt(
                        ExecutionRuleConfig.Rule::getPriority));

        GatewayLogger.info(
                log,
                "ExecutionRuleEngine initialized rules={}",
                this.rules.size());
    }

    public AggregatedResult evaluate(
            Map<String, String> responseCodes) {

        long start =
                System.nanoTime();

        GatewayLogger.debug(
                log,
                "Evaluating response codes {}",
                responseCodes);

        ExecutionRuleConfig.Rule defaultRule =
                null;

        for (ExecutionRuleConfig.Rule rule :
                rules) {

            /*
             * A rule without conditions is the default rule.
             */
            if (rule.getWhen() == null ||
                    rule.getWhen().isEmpty()) {

                defaultRule = rule;

                continue;
            }

            GatewayLogger.debug(
                    log,
                    "Evaluating rule priority={} conditions={}",
                    rule.getPriority(),
                    rule.getWhen());

            if (matches(
                    rule,
                    responseCodes)) {

                AggregatedResult result =
                        toResult(
                                rule.getResult());

                GatewayLogger.info(
                        log,
                        "Rule matched priority={} rc={} action={} settlement={} evaluation={}us",
                        rule.getPriority(),
                        result.responseCode(),
                        result.action(),
                        result.settlement(),
                        (System.nanoTime() - start)
                                / 1000);

                return result;
            }
        }

        /*
         * ---------------------------------------------------------
         * Default rule
         * ---------------------------------------------------------
         */

        if (defaultRule != null) {

            AggregatedResult result =
                    toResult(
                            defaultRule.getResult());

            GatewayLogger.warn(
                    log,
                    "No rule matched, using default rule rc={} action={} evaluation={}us",
                    result.responseCode(),
                    result.action(),
                    (System.nanoTime() - start)
                            / 1000);

            return result;
        }

        /*
         * ---------------------------------------------------------
         * No matching/default rule
         * ---------------------------------------------------------
         */

        GatewayLogger.error(
                log,
                "No rule matched and no default rule configured evaluation={}us",
                (System.nanoTime() - start)
                        / 1000);

        return new AggregatedResult(
                false,
                "96",
                "SYSTEM_ERROR",
                "REJECT",
                "H096",
                "NONE");
    }

    private boolean matches(
            ExecutionRuleConfig.Rule rule,
            Map<String, String> responseCodes) {

        for (Map.Entry<String, List<String>> condition :
                rule.getWhen().entrySet()) {

            String system =
                    condition.getKey();

            List<String> allowedCodes =
                    condition.getValue();

            String actualCode =
                    responseCodes.get(system);

            /*
             * Required downstream result is missing.
             */
            if (actualCode == null) {

                GatewayLogger.debug(
                        log,
                        "Rule priority={} failed missing system {}",
                        rule.getPriority(),
                        system);

                return false;
            }

            /*
             * Response code does not satisfy the rule.
             */
            if (!allowedCodes.contains(
                    actualCode)) {

                GatewayLogger.debug(
                        log,
                        "Rule priority={} failed system={} actual={} expected={}",
                        rule.getPriority(),
                        system,
                        actualCode,
                        allowedCodes);

                return false;
            }
        }

        return true;
    }

    private AggregatedResult toResult(
            ExecutionRuleConfig.RuleResult result) {

        boolean success =
                "00".equals(
                        result.getResponseCode());

        return new AggregatedResult(
                success,
                result.getResponseCode(),
                result.getMessage(),
                result.getAction(),
                result.getHostCode(),
                result.getSettlement());
    }
}