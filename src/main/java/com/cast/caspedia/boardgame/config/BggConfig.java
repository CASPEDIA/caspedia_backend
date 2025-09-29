package com.cast.caspedia.boardgame.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BggConfig {
    @Getter
    @Setter
    @Component
    public class BggProperties {
        private String url = "https://boardgamegeek.com/xmlapi2/thing";
        private int delaySeconds = 3;
        private int batchSize = 20;
        private int startId = 1;
        private int endId = 1000000;
        private int maxEmptyResponses = 1000;
    }

    /**
     * BGG API에 HTTP 요청을 보내기 위한 RestTemplate 빈을 생성합니다.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
