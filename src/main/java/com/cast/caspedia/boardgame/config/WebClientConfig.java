package com.cast.caspedia.boardgame.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient BGGWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl("https://boardgamegeek.com/xmlapi2") // 기본 URL 설정
                .defaultHeader("Content-Type", "application/xml") // 기본 헤더 설정
                .build();
    }
}