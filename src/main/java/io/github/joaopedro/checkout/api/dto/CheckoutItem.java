package io.github.joaopedro.checkout.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CheckoutItem(
        @Schema(example = "TECLADO-01") @NotBlank @Size(max = 40) String sku,
        @Schema(example = "Teclado mecanico") @NotBlank @Size(max = 120) String name,
        @Schema(example = "199.90") @NotNull @DecimalMin("0.01") @Digits(integer = 9, fraction = 2) BigDecimal unitPrice,
        @Schema(example = "1") @NotNull @Min(1) @Max(1000) Integer quantity,
        @Schema(example = "1.200") @NotNull @DecimalMin("0.000") @Digits(integer = 4, fraction = 3) BigDecimal weightKg) {
}
