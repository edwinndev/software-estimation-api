package com.intecx.inscope.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AiClientConfig {

    @Bean
    public WebClient aiWebClient(AiProperties aiProperties) {
        return WebClient.builder()
                .baseUrl(aiProperties.getBaseUrl())
                .build();
    }
}

