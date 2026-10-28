package io.github.joaopedro.checkout.discount;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DiscountStrategiesTest {

    private static DiscountContext context(String subtotal, int quantity) {
        return new DiscountContext(new BigDecimal(subtotal), quantity);
    }

    @Nested
    class Regular {

        private final RegularDiscount strategy = new RegularDiscount();

        @Test
        void neverDiscounts() {
            assertThat(strategy.key()).isEqualTo("REGULAR");
            assertThat(strategy.calculate(context("999.99", 50))).isEqualTo("0.00");
        }
    }

    @Nested
    class Vip {

        private final VipDiscount strategy = new VipDiscount();

        @Test
        void key() {
            assertThat(strategy.key()).isEqualTo("VIP");
        }

        @ParameterizedTest(name = "{0} -> {1}")
        @CsvSource({"100.00, 10.00", "199.90, 19.99", "0.05, 0.00", "0.15, 0.02", "0.25, 0.02"})
        void takesTenPercentWithHalfEvenRounding(String subtotal, String expected) {
            assertThat(strategy.calculate(context(subtotal, 1))).isEqualTo(expected);
        }
    }

    @Nested
    class Atacado {

        private final AtacadoDiscount strategy = new AtacadoDiscount();

        @Test
        void key() {
            assertThat(strategy.key()).isEqualTo("ATACADO");
        }

        @ParameterizedTest(name = "subtotal {0}, {1} units -> {2}")
        @CsvSource({"1000.00, 1, 50.00", "1000.00, 9, 50.00", "1000.00, 10, 150.00", "333.33, 25, 50.00"})
        void appliesBulkRateFromTenUnits(String subtotal, int quantity, String expected) {
            assertThat(strategy.calculate(context(subtotal, quantity))).isEqualTo(expected);
        }
    }

    @Nested
    class PrimeiraCompra {

        private final PrimeiraCompraDiscount strategy = new PrimeiraCompraDiscount();

        @Test
        void key() {
            assertThat(strategy.key()).isEqualTo("PRIMEIRA_COMPRA");
        }

        @ParameterizedTest(name = "{0} -> {1}")
        @CsvSource({"100.00, 20.00", "249.99, 50.00", "250.00, 50.00", "1000.00, 50.00"})
        void takesTwentyPercentCappedAtFifty(String subtotal, String expected) {
            assertThat(strategy.calculate(context(subtotal, 1))).isEqualTo(expected);
        }
    }
}
