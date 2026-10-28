package io.github.joaopedro.checkout.strategy;

import java.util.List;

public class UnknownStrategyException extends RuntimeException {

    private final String axis;
    private final String requestedKey;
    private final List<String> validOptions;

    public UnknownStrategyException(String axis, String requestedKey, List<String> validOptions) {
        super("Unknown strategy '%s' for %s. Valid options: %s".formatted(requestedKey, axis, validOptions));
        this.axis = axis;
        this.requestedKey = requestedKey;
        this.validOptions = List.copyOf(validOptions);
    }

    public String axis() {
        return axis;
    }

    public String requestedKey() {
        return requestedKey;
    }

    public List<String> validOptions() {
        return validOptions;
    }
}
