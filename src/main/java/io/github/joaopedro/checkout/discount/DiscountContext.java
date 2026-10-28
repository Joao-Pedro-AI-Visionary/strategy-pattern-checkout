package io.github.joaopedro.checkout.discount;

import java.math.BigDecimal;

/**
 * Input of a discount calculation.
 *
 * @param subtotal      sum of {@code unitPrice * quantity} of every item
 * @param totalQuantity sum of the item quantities
 */
public record DiscountContext(BigDecimal subtotal, int totalQuantity) {
}
