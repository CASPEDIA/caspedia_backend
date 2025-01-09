package com.cast.caspedia.security.jwt.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("com.cast.caspedia.security")
public class JwtProp {

    //어플리케이션프로퍼티의 시크릿키를 가져옴
    private String secretKey;
}
