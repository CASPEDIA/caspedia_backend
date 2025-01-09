package com.cast.caspedia.security.domain;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String userId;
    private String password;
}
