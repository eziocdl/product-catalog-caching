package com.github.eziocdl.catalog.infrastructure.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean

    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Catalog API (CQRS & Redis)")
                        .description("High-performance Product Catalog API.\n" +
                                "Key Features:\n" +
                                "- **Clean Architecture**: Domain isolation.\n" +
                                "- **CQRS**: Segregation of Commands (Write) and Queries (Read).\n" +
                                "- **Redis**: Look-aside caching strategy for high-speed reads.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Ezio Lima")
                                .url("https://github.com/eziocdl")
                                .email("eziocdl@gmail.com")));

    }
}
