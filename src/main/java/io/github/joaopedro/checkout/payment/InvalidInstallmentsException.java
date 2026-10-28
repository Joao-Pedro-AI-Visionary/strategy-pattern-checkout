package io.github.joaopedro.checkout.payment;

public class InvalidInstallmentsException extends RuntimeException {

    private final String paymentMethod;
    private final int requested;
    private final int max;

    public InvalidInstallmentsException(String paymentMethod, int requested, int max) {
        super("Payment method %s accepts 1 to %d installment(s), got %d".formatted(paymentMethod, max, requested));
        this.paymentMethod = paymentMethod;
        this.requested = requested;
        this.max = max;
    }

    public String paymentMethod() {
        return paymentMethod;
    }

    public int requested() {
        return requested;
    }

    public int max() {
        return max;
    }
}
