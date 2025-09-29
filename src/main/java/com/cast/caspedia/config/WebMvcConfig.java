package com.cast.caspedia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web MVC 설정 클래스입니다.
 * 이 클래스는 Spring MVC의 콘텐츠 협상 전략을 JSON을 기본값으로 설정하도록 구성합니다.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(false)
                .ignoreAcceptHeader(false)
                .defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // ⬇️ MVC 응답용 XML 컨버터 제거 (내부 XmlMapper와 무관)
        converters.removeIf(c -> c instanceof MappingJackson2XmlHttpMessageConverter);
    }
}