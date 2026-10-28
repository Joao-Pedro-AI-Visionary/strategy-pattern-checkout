package io.github.joaopedro.checkout.money;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MoneyTest {

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({"10, 10.00", "0.125, 0.12", "0.135, 0.14", "2.675, 2.68", "-0.125, -0.12"})
    void roundsToTwoDecimalsWithHalfEven(String value, String expected) {
        BigDecimal rounded = Money.round(new BigDecimal(value));

        assertThat(rounded).isEqualTo(expected);
        assertThat(rounded.scale()).isEqualTo(2);
    }

    @ParameterizedTest(name = "{1} of {0} -> {2}")
    @CsvSource({"200.00, 0.10, 20.00", "19.99, 0.05, 1.00", "0.50, 0.05, 0.02"})
    void percentOfRoundsResult(String amount, String rate, String expected) {
        assertThat(Money.percentOf(new BigDecimal(amount), new BigDecimal(rate))).isEqualTo(expected);
    }
}
