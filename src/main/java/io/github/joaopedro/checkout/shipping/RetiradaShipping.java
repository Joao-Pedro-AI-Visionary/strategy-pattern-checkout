package io.github.joaopedro.checkout.shipping;

import io.github.joaopedro.checkout.money.Money;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class RetiradaShipping implements ShippingStrategy {

    @Override
    public String key() {
        return "RETIRADA";
    }

    @Override
    public String description() {
        return "Store pickup, always free";
    }

    @Override
    public BigDecimal calculate(ShippingContext context) {
        return Money.ZERO;
    }
}
