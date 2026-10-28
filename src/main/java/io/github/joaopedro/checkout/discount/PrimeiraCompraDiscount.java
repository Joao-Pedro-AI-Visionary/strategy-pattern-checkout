package io.github.joaopedro.checkout.discount;

import io.github.joaopedro.checkout.money.Money;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class PrimeiraCompraDiscount implements DiscountStrategy {

    static final BigDecimal RATE = new BigDecimal("0.20");
    static final BigDecimal CAP = Money.of("50.00");

    @Override
    public String key() {
        return "PRIMEIRA_COMPRA";
    }

    @Override
    public String description() {
        return "First purchase: 20% off, limited to R$ 50,00";
    }

    @Override
    public BigDecimal calculate(DiscountContext context) {
        return Money.percentOf(context.subtotal(), RATE).min(CAP);
    }
}
