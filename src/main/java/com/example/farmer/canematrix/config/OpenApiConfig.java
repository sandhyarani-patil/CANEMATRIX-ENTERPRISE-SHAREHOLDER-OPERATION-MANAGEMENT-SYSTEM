package com.example.farmer.canematrix.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI caneMatrixOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CaneMatrix - Farmer Management APIs")
                        .description("REST APIs for Farmer, Bank, Farm Details, and Nominee Management")
                        .version("1.0.0"));
    }
}