package io.github.joaopedro.checkout.shipping;

import io.github.joaopedro.checkout.strategy.KeyedStrategy;
import java.math.BigDecimal;

/** Strategy for the shipping axis: one implementation per delivery region. */
public interface ShippingStrategy extends KeyedStrategy {

    /** Shipping cost with scale 2. */
    BigDecimal calculate(ShippingContext context);
}
