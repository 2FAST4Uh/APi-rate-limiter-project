package com.ratelimiter.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI adminOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Rate Limiter Admin Service")
                        .description("REST APIs for Client, Plan, Key, and Analytics Management")
                        .version("1.0.0"));
    }
}
