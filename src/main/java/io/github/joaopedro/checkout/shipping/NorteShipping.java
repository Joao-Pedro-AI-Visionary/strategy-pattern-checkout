package io.github.joaopedro.checkout.shipping;

import org.springframework.stereotype.Component;

@Component
public class NorteShipping extends WeightBasedShipping {

    public NorteShipping() {
        super("35.00", "5.00");
    }

    @Override
    public String key() {
        return "NORTE";
    }

    @Override
    public String description() {
        return "R$ 35,00 + R$ 5,00/kg";
    }
}
