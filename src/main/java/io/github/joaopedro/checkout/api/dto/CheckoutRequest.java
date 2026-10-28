package io.github.joaopedro.checkout.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CheckoutRequest(@NotEmpty @Size(max = 100) List<@NotNull @Valid CheckoutItem> items) {
}
