package io.github.joaopedro.checkout.discount;

import io.github.joaopedro.checkout.money.Money;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class VipDiscount implements DiscountStrategy {

    static final BigDecimal RATE = new BigDecimal("0.10");

    @Override
    public String key() {
        return "VIP";
    }

    @Override
    public String description() {
        return "10% off the subtotal";
    }

    @Override
    public BigDecimal calculate(DiscountContext context) {
        return Money.percentOf(context.subtotal(), RATE);
    }
}
