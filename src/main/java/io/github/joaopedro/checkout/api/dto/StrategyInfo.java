package io.github.joaopedro.checkout.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.joaopedro.checkout.payment.PaymentStrategy;
import io.github.joaopedro.checkout.strategy.KeyedStrategy;

/**
 * Public view of a strategy.
 *
 * @param maxInstallments only present for payment methods
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record StrategyInfo(String key, String description, Integer maxInstallments) {

    public static StrategyInfo from(KeyedStrategy strategy) {
        Integer maxInstallments = strategy instanceof PaymentStrategy payment ? payment.maxInstallments() : null;
        return new StrategyInfo(strategy.key(), strategy.description(), maxInstallments);
    }
}
