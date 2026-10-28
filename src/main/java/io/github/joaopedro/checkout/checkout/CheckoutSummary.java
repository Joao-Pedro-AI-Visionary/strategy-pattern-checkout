package io.github.joaopedro.checkout.checkout;

import java.math.BigDecimal;

/**
 * Full breakdown of a checkout. Money fields have scale 2 (HALF_EVEN).
 * {@code total = subtotal - discount + shipping + paymentAdjustment}.
 */
public record CheckoutSummary(
        String customerType,
        String region,
        String paymentMethod,
        int totalQuantity,
        BigDecimal totalWeightKg,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal shipping,
        BigDecimal paymentAdjustment,
        BigDecimal total,
        Installments installments) {

    public record Installments(int count, BigDecimal value) {
    }
}
