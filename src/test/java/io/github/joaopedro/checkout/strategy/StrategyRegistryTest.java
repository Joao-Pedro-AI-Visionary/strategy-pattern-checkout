package io.github.joaopedro.checkout.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StrategyRegistryTest {

    record FakeStrategy(String key) implements KeyedStrategy {
        @Override
        public String description() {
            return "fake " + key;
        }
    }

    private final FakeStrategy alpha = new FakeStrategy("ALPHA");
    private final FakeStrategy beta = new FakeStrategy("BETA");
    private final StrategyRegistry<FakeStrategy> registry = new StrategyRegistry<>("fake", List.of(beta, alpha));

    @Test
    void selectsStrategyByExactKey() {
        assertThat(registry.get("ALPHA")).isSameAs(alpha);
        assertThat(registry.get("BETA")).isSameAs(beta);
    }

    @ParameterizedTest
    @ValueSource(strings = {"alpha", "Alpha", "  alpha  "})
    void keyLookupIgnoresCaseAndSurroundingBlanks(String key) {
        assertThat(registry.get(key)).isSameAs(alpha);
    }

    @Test
    void unknownKeyThrowsWithAxisAndSortedValidOptions() {
        assertThatThrownBy(() -> registry.get("GAMMA"))
                .isInstanceOfSatisfying(UnknownStrategyException.class, ex -> {
                    assertThat(ex.axis()).isEqualTo("fake");
                    assertThat(ex.requestedKey()).isEqualTo("GAMMA");
                    assertThat(ex.validOptions()).containsExactly("ALPHA", "BETA");
                    assertThat(ex.getMessage())
                            .isEqualTo("Unknown strategy 'GAMMA' for fake. Valid options: [ALPHA, BETA]");
                });
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void nullOrBlankKeyIsUnknown(String key) {
        assertThatThrownBy(() -> registry.get(key)).isInstanceOf(UnknownStrategyException.class);
    }

    @Test
    void listsKeysAndStrategiesInAlphabeticalOrder() {
        assertThat(registry.keys()).containsExactly("ALPHA", "BETA");
        assertThat(registry.all()).containsExactly(alpha, beta);
        assertThat(registry.axis()).isEqualTo("fake");
    }

    @Test
    void rejectsDuplicateKeys() {
        List<FakeStrategy> duplicated = List.of(alpha, new FakeStrategy("alpha"));
        assertThatThrownBy(() -> new StrategyRegistry<>("fake", duplicated))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate fake strategy key 'ALPHA'");
    }

    @Test
    void rejectsEmptyRegistry() {
        assertThatThrownBy(() -> new StrategyRegistry<FakeStrategy>("fake", List.of()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No fake strategy registered");
    }
}
