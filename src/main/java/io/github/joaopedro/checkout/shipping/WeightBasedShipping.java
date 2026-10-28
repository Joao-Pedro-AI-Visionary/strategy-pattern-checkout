package io.github.joaopedro.checkout.shipping;

import io.github.joaopedro.checkout.money.Money;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Template for regions charged as {@code baseFee + billableKg * feePerKg}, where the
 * billable weight is the total weight rounded up to the next whole kilogram.
 */
public abstract class WeightBasedShipping implements ShippingStrategy {

    private final BigDecimal baseFee;
    private final BigDecimal feePerKg;

    protected WeightBasedShipping(String baseFee, String feePerKg) {
        this.baseFee = Money.of(baseFee);
        this.feePerKg = Money.of(feePerKg);
    }

    @Override
    public BigDecimal calculate(ShippingContext context) {
        BigDecimal billableKg = context.totalWeightKg().setScale(0, RoundingMode.CEILING);
        return Money.round(baseFee.add(feePerKg.multiply(billableKg)));
    }
}
