package com.cast.caspedia.security.jwt.constants;

public class JwtContstants {
    //로그인 필터 경로
    public static final String AUTH_LOGIN_URL = "/api/user/login";
    //토큰 헤더
    public static final String TOKEN_HEADER = "Authorization";
    //토큰 헤더의 접두사
    public static final String TOKEN_PREFIX = "Bearer ";
    // 토큰 타입
    public static final String TOKEN_TYPE = "JWT";
}
