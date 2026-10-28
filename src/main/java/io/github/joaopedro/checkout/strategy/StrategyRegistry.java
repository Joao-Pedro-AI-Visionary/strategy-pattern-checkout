package io.github.joaopedro.checkout.strategy;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Context of the Strategy pattern: indexes every implementation by its key and
 * resolves the right one at runtime. Adding a new strategy only requires a new
 * Spring bean; this class never changes (Open/Closed Principle).
 *
 * @param <T> strategy type handled by this registry
 */
public class StrategyRegistry<T extends KeyedStrategy> {

    private final String axis;
    private final Map<String, T> strategies;

    public StrategyRegistry(String axis, Collection<T> implementations) {
        this.axis = Objects.requireNonNull(axis, "axis");
        Map<String, T> index = new TreeMap<>();
        for (T strategy : implementations) {
            String key = normalize(strategy.key());
            T previous = index.putIfAbsent(key, strategy);
            if (previous != null) {
                throw new IllegalStateException("Duplicate %s strategy key '%s': %s and %s".formatted(
                        axis, key, previous.getClass().getSimpleName(), strategy.getClass().getSimpleName()));
            }
        }
        if (index.isEmpty()) {
            throw new IllegalStateException("No %s strategy registered".formatted(axis));
        }
        this.strategies = Map.copyOf(index);
    }

    /**
     * Resolves a strategy by key, ignoring case and surrounding blanks.
     *
     * @throws UnknownStrategyException when the key is null, blank or not registered
     */
    public T get(String key) {
        String normalized = key == null ? "" : normalize(key);
        T strategy = strategies.get(normalized);
        if (strategy == null) {
            throw new UnknownStrategyException(axis, key, keys());
        }
        return strategy;
    }

    public String axis() {
        return axis;
    }

    /** Registered keys in alphabetical order. */
    public List<String> keys() {
        return strategies.keySet().stream().sorted().toList();
    }

    /** Registered strategies ordered by key. */
    public List<T> all() {
        return keys().stream().map(strategies::get).toList();
    }

    private static String normalize(String key) {
        return key.trim().toUpperCase(Locale.ROOT);
    }
}
