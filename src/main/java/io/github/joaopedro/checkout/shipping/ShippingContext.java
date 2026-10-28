package io.github.joaopedro.checkout.shipping;

import java.math.BigDecimal;

/**
 * Input of a shipping calculation.
 *
 * @param subtotal      order amount after the customer discount
 * @param totalWeightKg sum of the item weights, in kilograms
 */
public record ShippingContext(BigDecimal subtotal, BigDecimal totalWeightKg) {
}
