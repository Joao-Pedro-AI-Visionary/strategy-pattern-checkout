package io.github.joaopedro.checkout.api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
class CheckoutControllerIntegrationTest {

    private static final String CART = """
            {
              "items": [
                {"sku": "TECLADO-01", "name": "Teclado mecanico", "unitPrice": 199.90, "quantity": 1, "weightKg": 1.200},
                {"sku": "MOUSE-01", "name": "Mouse sem fio", "unitPrice": 89.90, "quantity": 2, "weightKg": 0.150}
              ]
            }
            """;

    private static final String BULK_CART = """
            {"items": [{"sku": "CABO-USB", "name": "Cabo USB-C", "unitPrice": 49.90, "quantity": 12, "weightKg": 0.800}]}
            """;

    @Autowired
    private MockMvc mockMvc;

    private static MockHttpServletRequestBuilder checkout(String body) {
        return post("/api/checkout").contentType(MediaType.APPLICATION_JSON).content(body);
    }

    @Test
    void vipInSudestePayingWithCardInSixInstallments() throws Exception {
        mockMvc.perform(checkout(CART)
                        .header("X-Customer-Type", "VIP")
                        .param("region", "SUDESTE")
                        .param("paymentMethod", "CARTAO")
                        .param("installments", "6"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "customerType": "VIP", "region": "SUDESTE", "paymentMethod": "CARTAO",
                          "totalQuantity": 3, "totalWeightKg": 1.500,
                          "subtotal": 379.70, "discount": 37.97, "shipping": 0.00,
                          "paymentAdjustment": 24.21, "total": 365.94,
                          "installments": {"count": 6, "value": 60.99}
                        }
                        """, JsonCompareMode.STRICT));
    }

    @Test
    void customerTypeDefaultsToRegularAndPixGivesDiscount() throws Exception {
        mockMvc.perform(checkout(CART).param("region", "NORDESTE").param("paymentMethod", "PIX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerType").value("REGULAR"))
                .andExpect(jsonPath("$.discount").value(0.00))
                .andExpect(jsonPath("$.shipping").value(32.00))
                .andExpect(jsonPath("$.paymentAdjustment").value(-20.58))
                .andExpect(jsonPath("$.total").value(391.12))
                .andExpect(jsonPath("$.installments.count").value(1));
    }

    @Test
    void wholesaleInNortePayingWithBoleto() throws Exception {
        mockMvc.perform(checkout(BULK_CART)
                        .header("X-Customer-Type", "ATACADO")
                        .param("region", "NORTE")
                        .param("paymentMethod", "BOLETO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtotal").value(598.80))
                .andExpect(jsonPath("$.discount").value(89.82))
                .andExpect(jsonPath("$.shipping").value(85.00))
                .andExpect(jsonPath("$.paymentAdjustment").value(3.49))
                .andExpect(jsonPath("$.total").value(597.47));
    }

    @Test
    void firstPurchaseWithPickupAndInterestFreeInstallmentsAcceptsLowerCaseKeys() throws Exception {
        mockMvc.perform(checkout(CART)
                        .header("X-Customer-Type", "primeira_compra")
                        .param("region", "retirada")
                        .param("paymentMethod", "cartao")
                        .param("installments", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerType").value("PRIMEIRA_COMPRA"))
                .andExpect(jsonPath("$.discount").value(50.00))
                .andExpect(jsonPath("$.shipping").value(0.00))
                .andExpect(jsonPath("$.paymentAdjustment").value(0.00))
                .andExpect(jsonPath("$.total").value(329.70))
                .andExpect(jsonPath("$.installments.value").value(109.90));
    }

    @Test
    void unknownRegionReturnsProblemDetailWithValidOptions() throws Exception {
        mockMvc.perform(checkout(CART).param("region", "SUL").param("paymentMethod", "PIX"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("urn:checkout:problem:unknown-strategy"))
                .andExpect(jsonPath("$.title").value("Unknown strategy"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.axis").value("region"))
                .andExpect(jsonPath("$.invalidValue").value("SUL"))
                .andExpect(jsonPath("$.validOptions").value(
                        containsInAnyOrder("NORDESTE", "NORTE", "RETIRADA", "SUDESTE")))
                .andExpect(jsonPath("$.instance").value("/api/checkout"));
    }

    @Test
    void unknownCustomerTypeHeaderReturns400() throws Exception {
        mockMvc.perform(checkout(CART)
                        .header("X-Customer-Type", "GOLD")
                        .param("region", "SUDESTE")
                        .param("paymentMethod", "PIX"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.axis").value("customerType"))
                .andExpect(jsonPath("$.validOptions").value(
                        containsInAnyOrder("ATACADO", "PRIMEIRA_COMPRA", "REGULAR", "VIP")));
    }

    @Test
    void unknownPaymentMethodReturns400() throws Exception {
        mockMvc.perform(checkout(CART).param("region", "SUDESTE").param("paymentMethod", "CHEQUE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.axis").value("paymentMethod"))
                .andExpect(jsonPath("$.detail").value(
                        "Unknown strategy 'CHEQUE' for paymentMethod. Valid options: [BOLETO, CARTAO, PIX]"));
    }

    @Test
    void invalidBodyReturnsValidationErrors() throws Exception {
        String body = """
                {"items": [{"sku": "", "name": "Produto", "unitPrice": -1, "quantity": 0, "weightKg": 1.0}]}
                """;
        mockMvc.perform(checkout(body).param("region", "SUDESTE").param("paymentMethod", "PIX"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("urn:checkout:problem:validation"))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors", hasSize(3)))
                .andExpect(jsonPath("$.errors[*].field").value(
                        containsInAnyOrder("items[0].quantity", "items[0].sku", "items[0].unitPrice")));
    }

    @Test
    void emptyCartReturns400() throws Exception {
        mockMvc.perform(checkout("{\"items\": []}").param("region", "SUDESTE").param("paymentMethod", "PIX"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("items"));
    }

    @Test
    void installmentsAboveTwelveReturns400() throws Exception {
        mockMvc.perform(checkout(CART)
                        .param("region", "SUDESTE")
                        .param("paymentMethod", "CARTAO")
                        .param("installments", "13"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("installments"));
    }

    @Test
    void pixWithInstallmentsReturns400() throws Exception {
        mockMvc.perform(checkout(CART)
                        .param("region", "SUDESTE")
                        .param("paymentMethod", "PIX")
                        .param("installments", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("urn:checkout:problem:invalid-installments"))
                .andExpect(jsonPath("$.maxInstallments").value(1));
    }

    @Test
    void missingRegionReturns400() throws Exception {
        mockMvc.perform(checkout(CART).param("paymentMethod", "PIX"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'region' is not present."));
    }

    @Test
    void malformedJsonReturns400() throws Exception {
        mockMvc.perform(checkout("{not json").param("region", "SUDESTE").param("paymentMethod", "PIX"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
