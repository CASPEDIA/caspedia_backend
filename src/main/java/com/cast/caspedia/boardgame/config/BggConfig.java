package com.cast.caspedia.boardgame.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
        private int maxEmptyResponses = 10000;
    }

    /**
     * BGG API에 HTTP 요청을 보내기 위한 RestTemplate 빈을 생성합니다.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * XML 데이터를 Java 객체로 변환하기 위한 XmlMapper 빈을 생성합니다.
     * 알 수 없는 속성이 있어도 오류를 내지 않도록 설정합니다.
     */
    @Bean
    public XmlMapper xmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return xmlMapper;
    }

    /**
     * Java 객체를 JSON 문자열로 변환하기 위한 일반 ObjectMapper 빈을 생성합니다.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
