package io.github.joaopedro.checkout.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@OpenAPIDefinition(info = @Info(
        title = "Strategy Pattern Checkout API",
        version = "1.0.0",
        description = "Checkout calculation where shipping, discount and payment rules are Strategy implementations"
                + " selected at runtime by request parameter or header.",
        contact = @Contact(name = "Joao Pedro Teixeira Rocha",
                url = "https://github.com/Joao-Pedro-AI-Visionary/strategy-pattern-checkout"),
        license = @License(name = "MIT", url = "https://opensource.org/licenses/MIT")))
public class OpenApiConfig {
}
