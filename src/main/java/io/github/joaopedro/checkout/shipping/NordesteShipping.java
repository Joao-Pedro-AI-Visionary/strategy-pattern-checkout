package io.github.joaopedro.checkout.shipping;

import org.springframework.stereotype.Component;

@Component
public class NordesteShipping extends WeightBasedShipping {

    public NordesteShipping() {
        super("25.00", "3.50");
    }

    @Override
    public String key() {
        return "NORDESTE";
    }

    @Override
    public String description() {
        return "R$ 25,00 + R$ 3,50/kg";
    }
}
