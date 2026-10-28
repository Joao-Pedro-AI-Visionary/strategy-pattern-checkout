package io.github.joaopedro.checkout.api;

import io.github.joaopedro.checkout.api.dto.StrategyInfo;
import io.github.joaopedro.checkout.discount.DiscountStrategy;
import io.github.joaopedro.checkout.payment.PaymentStrategy;
import io.github.joaopedro.checkout.shipping.ShippingStrategy;
import io.github.joaopedro.checkout.strategy.KeyedStrategy;
import io.github.joaopedro.checkout.strategy.StrategyRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/strategies", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Strategies", description = "Strategies available on each axis")
public class StrategyController {

    private final StrategyRegistry<ShippingStrategy> shippingRegistry;
    private final StrategyRegistry<DiscountStrategy> discountRegistry;
    private final StrategyRegistry<PaymentStrategy> paymentRegistry;

    public StrategyController(StrategyRegistry<ShippingStrategy> shippingRegistry,
                              StrategyRegistry<DiscountStrategy> discountRegistry,
                              StrategyRegistry<PaymentStrategy> paymentRegistry) {
        this.shippingRegistry = shippingRegistry;
        this.discountRegistry = discountRegistry;
        this.paymentRegistry = paymentRegistry;
    }

    @GetMapping
    @Operation(summary = "Lists every axis with its strategies, keyed by the input that selects them")
    public Map<String, List<StrategyInfo>> all() {
        Map<String, List<StrategyInfo>> axes = new LinkedHashMap<>();
        for (StrategyRegistry<?> registry : List.of(discountRegistry, shippingRegistry, paymentRegistry)) {
            axes.put(registry.axis(), describe(registry));
        }
        return axes;
    }

    @GetMapping("/shipping")
    @Operation(summary = "Shipping strategies (query param region)")
    public List<StrategyInfo> shipping() {
        return describe(shippingRegistry);
    }

    @GetMapping("/discounts")
    @Operation(summary = "Discount strategies (header X-Customer-Type)")
    public List<StrategyInfo> discounts() {
        return describe(discountRegistry);
    }

    @GetMapping("/payments")
    @Operation(summary = "Payment strategies (query params paymentMethod and installments)")
    public List<StrategyInfo> payments() {
        return describe(paymentRegistry);
    }

    private static List<StrategyInfo> describe(StrategyRegistry<? extends KeyedStrategy> registry) {
        return registry.all().stream().map(StrategyInfo::from).toList();
    }
}
