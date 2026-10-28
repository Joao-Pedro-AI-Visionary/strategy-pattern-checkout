package io.github.joaopedro.checkout.checkout;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.joaopedro.checkout.api.dto.CheckoutItem;
import io.github.joaopedro.checkout.discount.AtacadoDiscount;
import io.github.joaopedro.checkout.discount.PrimeiraCompraDiscount;
import io.github.joaopedro.checkout.discount.RegularDiscount;
import io.github.joaopedro.checkout.discount.VipDiscount;
import io.github.joaopedro.checkout.payment.BoletoPayment;
import io.github.joaopedro.checkout.payment.CartaoPayment;
import io.github.joaopedro.checkout.payment.PixPayment;
import io.github.joaopedro.checkout.shipping.NordesteShipping;
import io.github.joaopedro.checkout.shipping.NorteShipping;
import io.github.joaopedro.checkout.shipping.RetiradaShipping;
import io.github.joaopedro.checkout.shipping.SudesteShipping;
import io.github.joaopedro.checkout.strategy.StrategyRegistry;
import io.github.joaopedro.checkout.strategy.UnknownStrategyException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class CheckoutServiceTest {

    private static final List<CheckoutItem> CART = List.of(
            new CheckoutItem("TECLADO-01", "Teclado mecanico", new BigDecimal("199.90"), 1, new BigDecimal("1.200")),
            new CheckoutItem("MOUSE-01", "Mouse sem fio", new BigDecimal("89.90"), 2, new BigDecimal("0.150")));

    private final CheckoutService service = new CheckoutService(
            new StrategyRegistry<>("region", List.of(
                    new SudesteShipping(), new NordesteShipping(), new NorteShipping(), new RetiradaShipping())),
            new StrategyRegistry<>("customerType", List.of(
                    new RegularDiscount(), new VipDiscount(), new AtacadoDiscount(), new PrimeiraCompraDiscount())),
            new StrategyRegistry<>("paymentMethod", List.of(
                    new PixPayment(), new CartaoPayment(), new BoletoPayment())));

    @Test
    void combinesTheThreeStrategies() {
        CheckoutSummary summary = service.checkout(CART, "vip", "sudeste", "cartao", 6);

        assertThat(summary.customerType()).isEqualTo("VIP");
        assertThat(summary.region()).isEqualTo("SUDESTE");
        assertThat(summary.paymentMethod()).isEqualTo("CARTAO");
        assertThat(summary.totalQuantity()).isEqualTo(3);
        assertThat(summary.totalWeightKg()).isEqualTo("1.500");
        assertThat(summary.subtotal()).isEqualTo("379.70");
        assertThat(summary.discount()).isEqualTo("37.97");
        assertThat(summary.shipping()).isEqualTo("0.00");
        assertThat(summary.paymentAdjustment()).isEqualTo("24.21");
        assertThat(summary.total()).isEqualTo("365.94");
        assertThat(summary.installments().count()).isEqualTo(6);
        assertThat(summary.installments().value()).isEqualTo("60.99");
    }

    @Test
    void totalIsSubtotalMinusDiscountPlusShippingPlusAdjustment() {
        CheckoutSummary summary = service.checkout(CART, "REGULAR", "NORDESTE", "PIX", 1);

        assertThat(summary.subtotal()).isEqualTo("379.70");
        assertThat(summary.discount()).isEqualTo("0.00");
        assertThat(summary.shipping()).isEqualTo("32.00");
        assertThat(summary.paymentAdjustment()).isEqualTo("-20.58");
        assertThat(summary.total()).isEqualTo("391.12");
        assertThat(summary.subtotal().subtract(summary.discount()).add(summary.shipping())
                .add(summary.paymentAdjustment())).isEqualTo(summary.total());
    }

    @Test
    void unknownKeyFailsBeforeAnyCalculation() {
        assertThatThrownBy(() -> service.checkout(CART, "REGULAR", "SUL", "PIX", 1))
                .isInstanceOfSatisfying(UnknownStrategyException.class, ex -> {
                    assertThat(ex.axis()).isEqualTo("region");
                    assertThat(ex.validOptions()).containsExactly("NORDESTE", "NORTE", "RETIRADA", "SUDESTE");
                });
    }
}
