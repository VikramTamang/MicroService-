package com.project.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .description("Microservice for order creation, state lifecycle, and resilient inter-service stock orchestration.")
                        .version("v1.0.0")
                        .contact(new Contact().name("Engineering Team").email("dev@example.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
