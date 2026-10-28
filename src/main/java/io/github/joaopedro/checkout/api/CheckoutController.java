package io.github.joaopedro.checkout.api;

import io.github.joaopedro.checkout.api.dto.CheckoutRequest;
import io.github.joaopedro.checkout.checkout.CheckoutService;
import io.github.joaopedro.checkout.checkout.CheckoutSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/checkout", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Checkout", description = "Full checkout calculation")
public class CheckoutController {

    public static final String CUSTOMER_TYPE_HEADER = "X-Customer-Type";

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Calculates subtotal, discount, shipping, payment adjustment, total and installments",
            description = "Each axis is resolved at runtime: customer type by header, region and payment method"
                    + " by query parameter. Unknown keys return 400 with the list of valid options.")
    public CheckoutSummary checkout(
            @Parameter(description = "Customer type (discount strategy)", example = "VIP")
            @RequestHeader(name = CUSTOMER_TYPE_HEADER, defaultValue = "REGULAR") String customerType,
            @Parameter(description = "Delivery region (shipping strategy)", example = "SUDESTE")
            @RequestParam String region,
            @Parameter(description = "Payment method (payment strategy)", example = "CARTAO")
            @RequestParam String paymentMethod,
            @Parameter(description = "Number of installments", example = "1")
            @RequestParam(defaultValue = "1") @Min(1) @Max(12) int installments,
            @Valid @RequestBody CheckoutRequest request) {
        return checkoutService.checkout(request.items(), customerType, region, paymentMethod, installments);
    }
}
