package io.github.joaopedro.checkout.payment;

import java.math.BigDecimal;

/**
 * Outcome of applying a payment method to an amount.
 *
 * @param adjustment       amount added (fee/interest, positive) or removed (discount, negative)
 * @param installments     number of installments
 * @param installmentValue value of each installment
 * @param total            final amount charged to the customer
 */
public record PaymentResult(BigDecimal adjustment, int installments, BigDecimal installmentValue, BigDecimal total) {
}
