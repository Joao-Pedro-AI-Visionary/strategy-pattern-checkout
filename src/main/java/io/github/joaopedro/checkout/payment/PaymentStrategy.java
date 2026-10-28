package io.github.joaopedro.checkout.payment;

import io.github.joaopedro.checkout.strategy.KeyedStrategy;
import java.math.BigDecimal;

/** Strategy for the payment axis: one implementation per payment method. */
public interface PaymentStrategy extends KeyedStrategy {

    /** Highest number of installments accepted by the method. */
    int maxInstallments();

    /**
     * Applies the payment rule to the amount due.
     *
     * @throws InvalidInstallmentsException when {@code installments} is outside {@code 1..maxInstallments()}
     */
    PaymentResult apply(BigDecimal amount, int installments);

    default void checkInstallments(int installments) {
        if (installments < 1 || installments > maxInstallments()) {
            throw new InvalidInstallmentsException(key(), installments, maxInstallments());
        }
    }
}
