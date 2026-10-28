package io.github.joaopedro.checkout.discount;

import io.github.joaopedro.checkout.strategy.KeyedStrategy;
import java.math.BigDecimal;

/** Strategy for the discount axis: one implementation per customer type. */
public interface DiscountStrategy extends KeyedStrategy {

    /** Discount amount (positive value, scale 2) to subtract from the subtotal. */
    BigDecimal calculate(DiscountContext context);
}
