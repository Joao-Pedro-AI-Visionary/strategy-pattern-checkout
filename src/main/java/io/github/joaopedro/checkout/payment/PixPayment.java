package io.github.joaopedro.checkout.payment;

import io.github.joaopedro.checkout.money.Money;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class PixPayment implements PaymentStrategy {

    static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.05");

    @Override
    public String key() {
        return "PIX";
    }

    @Override
    public String description() {
        return "Instant payment with 5% off, single installment";
    }

    @Override
    public int maxInstallments() {
        return 1;
    }

    @Override
    public PaymentResult apply(BigDecimal amount, int installments) {
        checkInstallments(installments);
        BigDecimal discount = Money.percentOf(amount, DISCOUNT_RATE);
        BigDecimal total = Money.round(amount.subtract(discount));
        return new PaymentResult(discount.negate(), 1, total, total);
    }
}
