package io.github.joaopedro.checkout.payment;

import io.github.joaopedro.checkout.money.Money;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class BoletoPayment implements PaymentStrategy {

    static final BigDecimal ISSUANCE_FEE = Money.of("3.49");

    @Override
    public String key() {
        return "BOLETO";
    }

    @Override
    public String description() {
        return "Bank slip with a R$ 3,49 issuance fee, single installment";
    }

    @Override
    public int maxInstallments() {
        return 1;
    }

    @Override
    public PaymentResult apply(BigDecimal amount, int installments) {
        checkInstallments(installments);
        BigDecimal total = Money.round(amount.add(ISSUANCE_FEE));
        return new PaymentResult(ISSUANCE_FEE, 1, total, total);
    }
}
