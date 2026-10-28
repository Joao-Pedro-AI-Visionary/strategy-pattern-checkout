package io.github.joaopedro.checkout.strategy;

/**
 * Common contract for every strategy that can be selected at runtime by a key.
 */
public interface KeyedStrategy {

    /** Unique, upper-case key used to select the strategy (e.g. {@code PIX}). */
    String key();

    /** Human readable explanation of the rule implemented by the strategy. */
    String description();
}
