package io.github.joaopedro.checkout.discount;

import io.github.joaopedro.checkout.money.Money;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class RegularDiscount implements DiscountStrategy {

    @Override
    public String key() {
        return "REGULAR";
    }

    @Override
    public String description() {
        return "No discount";
    }

    @Override
    public BigDecimal calculate(DiscountContext context) {
        return Money.ZERO;
    }
}
