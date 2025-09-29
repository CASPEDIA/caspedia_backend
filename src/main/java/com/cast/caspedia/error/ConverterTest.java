package com.cast.caspedia.error;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
class ConverterTest {
    private final List<HttpMessageConverter<?>> converters;

    @PostConstruct
    void logConverters() {
        converters.forEach(c -> System.out.println("[HTTP Converter] " + c.getClass().getName()));
    }
}