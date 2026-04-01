package com.odinlascience.backend.modules.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AiModuleConfig {

    @Bean
    public RestClient aiRestClient() {
        return RestClient.builder().build();
    }
}
