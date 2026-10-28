package io.github.joaopedro.checkout.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ShippingStrategiesTest {

    private static ShippingContext context(String subtotal, String weightKg) {
        return new ShippingContext(new BigDecimal(subtotal), new BigDecimal(weightKg));
    }

    @Nested
    class Sudeste {

        private final SudesteShipping strategy = new SudesteShipping();

        @Test
        void key() {
            assertThat(strategy.key()).isEqualTo("SUDESTE");
        }

        @ParameterizedTest(name = "subtotal {0}, {1} kg -> {2}")
        @CsvSource({
                "100.00, 0.000, 15.00",
                "100.00, 0.300, 17.00",
                "100.00, 1.000, 17.00",
                "100.00, 2.500, 21.00",
                "298.99, 2.500, 21.00",
                "299.00, 2.500, 0.00",
                "1500.00, 30.000, 0.00"
        })
        void chargesBasePlusPerKgUntilFreeShippingThreshold(String subtotal, String weight, String expected) {
            assertThat(strategy.calculate(context(subtotal, weight))).isEqualTo(expected);
        }
    }

    @Nested
    class Nordeste {

        private final NordesteShipping strategy = new NordesteShipping();

        @Test
        void key() {
            assertThat(strategy.key()).isEqualTo("NORDESTE");
        }

        @ParameterizedTest(name = "{0} kg -> {1}")
        @CsvSource({"0.000, 25.00", "0.500, 28.50", "2.500, 35.50", "10.000, 60.00"})
        void chargesBasePlusPerBillableKg(String weight, String expected) {
            assertThat(strategy.calculate(context("1000.00", weight))).isEqualTo(expected);
        }
    }

    @Nested
    class Norte {

        private final NorteShipping strategy = new NorteShipping();

        @Test
        void key() {
            assertThat(strategy.key()).isEqualTo("NORTE");
        }

        @ParameterizedTest(name = "{0} kg -> {1}")
        @CsvSource({"0.000, 35.00", "0.500, 40.00", "2.500, 50.00", "10.000, 85.00"})
        void chargesBasePlusPerBillableKg(String weight, String expected) {
            assertThat(strategy.calculate(context("1000.00", weight))).isEqualTo(expected);
        }
    }

    @Nested
    class Retirada {

        private final RetiradaShipping strategy = new RetiradaShipping();

        @Test
        void key() {
            assertThat(strategy.key()).isEqualTo("RETIRADA");
        }

        @ParameterizedTest(name = "subtotal {0}, {1} kg -> free")
        @CsvSource({"10.00, 0.000", "100.00, 50.000"})
        void isAlwaysFree(String subtotal, String weight) {
            assertThat(strategy.calculate(context(subtotal, weight))).isEqualTo("0.00");
        }
    }
}
