package io.github.joaopedro.checkout.shipping;

import io.github.joaopedro.checkout.money.Money;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class SudesteShipping extends WeightBasedShipping {

    static final BigDecimal FREE_SHIPPING_THRESHOLD = Money.of("299.00");

    public SudesteShipping() {
        super("15.00", "2.00");
    }

    @Override
    public String key() {
        return "SUDESTE";
    }

    @Override
    public String description() {
        return "R$ 15,00 + R$ 2,00/kg; free shipping for orders from R$ 299,00";
    }

    @Override
    public BigDecimal calculate(ShippingContext context) {
        if (context.subtotal().compareTo(FREE_SHIPPING_THRESHOLD) >= 0) {
            return Money.ZERO;
        }
        return super.calculate(context);
    }
}
