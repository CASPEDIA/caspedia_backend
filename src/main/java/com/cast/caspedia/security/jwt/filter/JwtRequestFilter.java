package com.cast.caspedia.security.jwt.filter;

import com.cast.caspedia.security.constants.SecurityConstants;
import com.cast.caspedia.security.jwt.provider.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {


    private final JwtTokenProvider jwtTokenProvider;

    // 생성자
    public JwtRequestFilter(JwtTokenProvider jwtTokenProvider ) {

        this.jwtTokenProvider = jwtTokenProvider;
    }


    // ✅ Request 헤더에서 JWT 토큰을 꺼내 인증작업 진행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        if ("/api/user/login".equals(requestPath)) {
            // 로그인 요청은 토큰 검증을 하지 않음
            log.info("로그인 요청은 토큰 검증을 하지 않습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // HTTP 헤더에서 토큰을 가져옴
        String header = request.getHeader(SecurityConstants.TOKEN_HEADER);
        log.info("authorization : " + header);


        //✅ Bearer + {jwt} 체크
        // 헤더가 없거나 형식이 올바르지 않으면 다음 필터로 진행
        if (header == null || header.length() == 0 || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"유효하지 않은 토큰입니다.\"}");
            return;
        }

        // Bearer + ${jwt} ➡ "Bearer " 제거
        String jwt = header.replace(SecurityConstants.TOKEN_PREFIX, "");

        // 토큰을 사용하여 Authentication 객체 생성
        Authentication authentication = jwtTokenProvider.getAuthentication(jwt);

        // 토큰 유효 검사 (토큰이 만료되지 않았으면)
        if( jwtTokenProvider.validateToken(jwt) ) {
            log.info("유효한 JWT 토큰입니다.");
            // 👩‍💼 [로그인]
            // SecurityContextHolder(사용자 보안정보를 담는 객체)에
            // Authentication(사용자 인증 정보) 객체를 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            log.info("만료된 JWT 토큰입니다.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"만료된 토큰입니다.\"}");
            return;
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}

