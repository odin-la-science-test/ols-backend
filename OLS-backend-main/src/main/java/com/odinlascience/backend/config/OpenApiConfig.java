package com.odinlascience.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI odinLaScienceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Odin La Science API")
                        .description("Backend pour la plateforme Munin Atlas & Hugin Lab.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Support Technique")
                                .email("admin@odinlascience.com")
                                .url("https://odinlascience.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}