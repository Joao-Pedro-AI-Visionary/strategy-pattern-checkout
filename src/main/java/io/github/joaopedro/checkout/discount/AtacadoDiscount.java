package io.github.joaopedro.checkout.discount;

import io.github.joaopedro.checkout.money.Money;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class AtacadoDiscount implements DiscountStrategy {

    static final int BULK_QUANTITY = 10;
    static final BigDecimal BULK_RATE = new BigDecimal("0.15");
    static final BigDecimal BASE_RATE = new BigDecimal("0.05");

    @Override
    public String key() {
        return "ATACADO";
    }

    @Override
    public String description() {
        return "Wholesale: 15% off from 10 units, 5% below that";
    }

    @Override
    public BigDecimal calculate(DiscountContext context) {
        BigDecimal rate = context.totalQuantity() >= BULK_QUANTITY ? BULK_RATE : BASE_RATE;
        return Money.percentOf(context.subtotal(), rate);
    }
}
