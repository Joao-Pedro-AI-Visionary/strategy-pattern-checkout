package io.github.joaopedro.checkout.money;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Central place for monetary rules: every amount exposed by the API has scale 2
 * and is rounded with {@link RoundingMode#HALF_EVEN} (banker's rounding).
 */
public final class Money {

    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;
    public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(SCALE, ROUNDING);

    private Money() {
    }

    public static BigDecimal of(String value) {
        return round(new BigDecimal(value));
    }

    public static BigDecimal round(BigDecimal value) {
        return value.setScale(SCALE, ROUNDING);
    }

    /** Applies a percentage expressed as a fraction (0.10 = 10%) and rounds the result. */
    public static BigDecimal percentOf(BigDecimal amount, BigDecimal rate) {
        return round(amount.multiply(rate));
    }
}
