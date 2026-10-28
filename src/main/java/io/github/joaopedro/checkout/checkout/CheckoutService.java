package io.github.joaopedro.checkout.checkout;

import io.github.joaopedro.checkout.api.dto.CheckoutItem;
import io.github.joaopedro.checkout.discount.DiscountContext;
import io.github.joaopedro.checkout.discount.DiscountStrategy;
import io.github.joaopedro.checkout.money.Money;
import io.github.joaopedro.checkout.payment.PaymentResult;
import io.github.joaopedro.checkout.payment.PaymentStrategy;
import io.github.joaopedro.checkout.shipping.ShippingContext;
import io.github.joaopedro.checkout.shipping.ShippingStrategy;
import io.github.joaopedro.checkout.strategy.StrategyRegistry;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the three strategy axes. There is no {@code if/else} or {@code switch}
 * on region, customer type or payment method: each rule lives in its own strategy.
 */
@Service
public class CheckoutService {

    private final StrategyRegistry<ShippingStrategy> shippingRegistry;
    private final StrategyRegistry<DiscountStrategy> discountRegistry;
    private final StrategyRegistry<PaymentStrategy> paymentRegistry;

    public CheckoutService(StrategyRegistry<ShippingStrategy> shippingRegistry,
                           StrategyRegistry<DiscountStrategy> discountRegistry,
                           StrategyRegistry<PaymentStrategy> paymentRegistry) {
        this.shippingRegistry = shippingRegistry;
        this.discountRegistry = discountRegistry;
        this.paymentRegistry = paymentRegistry;
    }

    public CheckoutSummary checkout(List<CheckoutItem> items, String customerType, String region,
                                    String paymentMethod, int installments) {
        DiscountStrategy discountStrategy = discountRegistry.get(customerType);
        ShippingStrategy shippingStrategy = shippingRegistry.get(region);
        PaymentStrategy paymentStrategy = paymentRegistry.get(paymentMethod);

        BigDecimal subtotal = Money.round(items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        int totalQuantity = items.stream().mapToInt(CheckoutItem::quantity).sum();
        BigDecimal totalWeightKg = items.stream()
                .map(item -> item.weightKg().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(3, Money.ROUNDING);

        BigDecimal discount = discountStrategy.calculate(new DiscountContext(subtotal, totalQuantity));
        BigDecimal discountedSubtotal = subtotal.subtract(discount);
        BigDecimal shipping = shippingStrategy.calculate(new ShippingContext(discountedSubtotal, totalWeightKg));
        PaymentResult payment = paymentStrategy.apply(discountedSubtotal.add(shipping), installments);

        return new CheckoutSummary(
                discountStrategy.key(),
                shippingStrategy.key(),
                paymentStrategy.key(),
                totalQuantity,
                totalWeightKg,
                subtotal,
                discount,
                shipping,
                payment.adjustment(),
                payment.total(),
                new CheckoutSummary.Installments(payment.installments(), payment.installmentValue()));
    }
}
