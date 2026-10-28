package io.github.joaopedro.checkout.api;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class StrategyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listsShippingStrategies() throws Exception {
        mockMvc.perform(get("/api/strategies/shipping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].key").value(contains("NORDESTE", "NORTE", "RETIRADA", "SUDESTE")))
                .andExpect(jsonPath("$[0].description").isNotEmpty())
                .andExpect(jsonPath("$[0].maxInstallments").doesNotExist());
    }

    @Test
    void listsDiscountStrategies() throws Exception {
        mockMvc.perform(get("/api/strategies/discounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].key").value(contains("ATACADO", "PRIMEIRA_COMPRA", "REGULAR", "VIP")));
    }

    @Test
    void listsPaymentStrategiesWithMaxInstallments() throws Exception {
        mockMvc.perform(get("/api/strategies/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].key").value(contains("BOLETO", "CARTAO", "PIX")))
                .andExpect(jsonPath("$[*].maxInstallments").value(contains(1, 12, 1)));
    }

    @Test
    void listsEveryAxisKeyedByTheInputThatSelectsIt() throws Exception {
        mockMvc.perform(get("/api/strategies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerType", hasSize(4)))
                .andExpect(jsonPath("$.region", hasSize(4)))
                .andExpect(jsonPath("$.paymentMethod", hasSize(3)));
    }

    @Test
    void exposesOpenApiDocument() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Strategy Pattern Checkout API"))
                .andExpect(jsonPath("$.paths['/api/checkout'].post").exists());
    }
}
