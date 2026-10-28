package io.github.joaopedro.checkout.payment;

import io.github.joaopedro.checkout.money.Money;
import java.math.BigDecimal;
import java.math.MathContext;
import org.springframework.stereotype.Component;

/**
 * Credit card: up to 3 installments without interest; from 4 to 12 installments the
 * amount is financed with compound interest of 1.99% per month (Price table):
 * {@code installment = amount * i / (1 - (1 + i)^-n)}.
 */
@Component
public class CartaoPayment implements PaymentStrategy {

    static final int MAX_INSTALLMENTS = 12;
    static final int INTEREST_FREE_INSTALLMENTS = 3;
    static final BigDecimal MONTHLY_RATE = new BigDecimal("0.0199");
    private static final MathContext PRECISION = MathContext.DECIMAL64;

    @Override
    public String key() {
        return "CARTAO";
    }

    @Override
    public String description() {
        return "Credit card: up to 3x interest free, 4x to 12x with 1.99% monthly interest (Price table)";
    }

    @Override
    public int maxInstallments() {
        return MAX_INSTALLMENTS;
    }

    @Override
    public PaymentResult apply(BigDecimal amount, int installments) {
        checkInstallments(installments);
        BigDecimal principal = Money.round(amount);
        if (installments <= INTEREST_FREE_INSTALLMENTS) {
            BigDecimal installmentValue = principal.divide(BigDecimal.valueOf(installments), Money.SCALE, Money.ROUNDING);
            return new PaymentResult(Money.ZERO, installments, installmentValue, principal);
        }
        BigDecimal discountFactor = BigDecimal.ONE.add(MONTHLY_RATE).pow(-installments, PRECISION);
        BigDecimal installmentValue = Money.round(principal.multiply(MONTHLY_RATE, PRECISION)
                .divide(BigDecimal.ONE.subtract(discountFactor, PRECISION), PRECISION));
        BigDecimal total = Money.round(installmentValue.multiply(BigDecimal.valueOf(installments)));
        return new PaymentResult(Money.round(total.subtract(principal)), installments, installmentValue, total);
    }
}
