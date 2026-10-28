package io.github.joaopedro.checkout.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PaymentStrategiesTest {

    private static final BigDecimal THOUSAND = new BigDecimal("1000.00");

    @Nested
    class Pix {

        private final PixPayment strategy = new PixPayment();

        @Test
        void key() {
            assertThat(strategy.key()).isEqualTo("PIX");
            assertThat(strategy.maxInstallments()).isEqualTo(1);
        }

        @ParameterizedTest(name = "{0} -> discount {1}, total {2}")
        @CsvSource({"1000.00, -50.00, 950.00", "199.90, -10.00, 189.90", "0.30, -0.02, 0.28"})
        void appliesFivePercentDiscount(String amount, String adjustment, String total) {
            PaymentResult result = strategy.apply(new BigDecimal(amount), 1);

            assertThat(result.adjustment()).isEqualTo(adjustment);
            assertThat(result.total()).isEqualTo(total);
            assertThat(result.installments()).isEqualTo(1);
            assertThat(result.installmentValue()).isEqualTo(total);
        }

        @Test
        void rejectsInstallments() {
            assertThatThrownBy(() -> strategy.apply(THOUSAND, 2))
                    .isInstanceOf(InvalidInstallmentsException.class)
                    .hasMessage("Payment method PIX accepts 1 to 1 installment(s), got 2");
        }
    }

    @Nested
    class Boleto {

        private final BoletoPayment strategy = new BoletoPayment();

        @Test
        void addsIssuanceFee() {
            PaymentResult result = strategy.apply(THOUSAND, 1);

            assertThat(strategy.key()).isEqualTo("BOLETO");
            assertThat(result.adjustment()).isEqualTo("3.49");
            assertThat(result.total()).isEqualTo("1003.49");
            assertThat(result.installments()).isEqualTo(1);
            assertThat(result.installmentValue()).isEqualTo("1003.49");
        }

        @Test
        void rejectsInstallments() {
            assertThatThrownBy(() -> strategy.apply(THOUSAND, 3)).isInstanceOf(InvalidInstallmentsException.class);
        }
    }

    @Nested
    class Cartao {

        private final CartaoPayment strategy = new CartaoPayment();

        @Test
        void key() {
            assertThat(strategy.key()).isEqualTo("CARTAO");
            assertThat(strategy.maxInstallments()).isEqualTo(12);
        }

        @ParameterizedTest(name = "{0}x without interest -> {1} each")
        @CsvSource({"1, 1000.00", "2, 500.00", "3, 333.33"})
        void upToThreeInstallmentsHasNoInterest(int installments, String installmentValue) {
            PaymentResult result = strategy.apply(THOUSAND, installments);

            assertThat(result.adjustment()).isEqualTo("0.00");
            assertThat(result.total()).isEqualTo("1000.00");
            assertThat(result.installments()).isEqualTo(installments);
            assertThat(result.installmentValue()).isEqualTo(installmentValue);
        }

        // Expected values computed independently with Python Decimal (HALF_EVEN).
        @ParameterizedTest(name = "{0} in {1}x -> {2} each, total {3}")
        @CsvSource({
                "1000.00, 6, 178.47, 1070.82, 70.82",
                "1000.00, 12, 94.50, 1134.00, 134.00",
                "100.00, 4, 26.26, 105.04, 5.04",
                "500.00, 10, 55.63, 556.30, 56.30"
        })
        void fromFourInstallmentsAppliesPriceTableInterest(String amount, int installments, String installmentValue,
                                                           String total, String adjustment) {
            PaymentResult result = strategy.apply(new BigDecimal(amount), installments);

            assertThat(result.installmentValue()).isEqualTo(installmentValue);
            assertThat(result.total()).isEqualTo(total);
            assertThat(result.adjustment()).isEqualTo(adjustment);
            assertThat(result.installments()).isEqualTo(installments);
        }

        @ParameterizedTest(name = "{0} installments is invalid")
        @ValueSource(ints = {0, -1, 13})
        void rejectsInstallmentsOutsideRange(int installments) {
            assertThatThrownBy(() -> strategy.apply(THOUSAND, installments))
                    .isInstanceOfSatisfying(InvalidInstallmentsException.class, ex -> {
                        assertThat(ex.paymentMethod()).isEqualTo("CARTAO");
                        assertThat(ex.requested()).isEqualTo(installments);
                        assertThat(ex.max()).isEqualTo(12);
                    });
        }
    }
}
