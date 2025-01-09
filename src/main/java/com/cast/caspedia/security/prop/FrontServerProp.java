package com.cast.caspedia.security.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties("com.cast.caspedia.front")
public class FrontServerProp {
    private String urls;

    public List<String> getUrlList() {
        return List.of(urls.split(","));
    }
}
