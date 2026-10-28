package io.github.joaopedro.checkout.config;

import io.github.joaopedro.checkout.discount.DiscountStrategy;
import io.github.joaopedro.checkout.payment.PaymentStrategy;
import io.github.joaopedro.checkout.shipping.ShippingStrategy;
import io.github.joaopedro.checkout.strategy.StrategyRegistry;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds one registry per axis from every strategy bean found in the context.
 * The axis name matches the request input used to select the strategy.
 */
@Configuration(proxyBeanMethods = false)
public class StrategyConfig {

    public static final String REGION = "region";
    public static final String CUSTOMER_TYPE = "customerType";
    public static final String PAYMENT_METHOD = "paymentMethod";

    @Bean
    StrategyRegistry<ShippingStrategy> shippingRegistry(List<ShippingStrategy> strategies) {
        return new StrategyRegistry<>(REGION, strategies);
    }

    @Bean
    StrategyRegistry<DiscountStrategy> discountRegistry(List<DiscountStrategy> strategies) {
        return new StrategyRegistry<>(CUSTOMER_TYPE, strategies);
    }

    @Bean
    StrategyRegistry<PaymentStrategy> paymentRegistry(List<PaymentStrategy> strategies) {
        return new StrategyRegistry<>(PAYMENT_METHOD, strategies);
    }
}
