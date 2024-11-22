package com.cast.caspedia.security.jwt.filter;

import com.cast.caspedia.security.custom.CustomUserDetails;
import com.cast.caspedia.security.jwt.constants.JwtContstants;
import com.cast.caspedia.security.jwt.provider.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;

/*            (/api/user/login)
    client -> filter -> server
    attemptAuthentication -> userId, password 인증 시도

    - 인증성공-> JWT 생성 -> response > headers > authorization : Bearer {jwt}

    - 인증실패 -> response > status : 401
 */
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private ObjectMapper objectMapper;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl("/api/user/login");
    }


    //인증 시도 메서드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // JSON 데이터를 읽어 Map으로 변환
            Map<String, String> jsonRequest = objectMapper.readValue(request.getInputStream(), Map.class);
            String userId = jsonRequest.get("id");
            String password = jsonRequest.get("password");

            log.info("userId: {}, password: {}", userId, password);
            // 사용자 인증정보 객체 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(userId, password);

            // 사용자 인증정보 객체를 인증매니저에게 전달하여 인증을 요청
            authentication = authenticationManager.authenticate(authentication);
            log.info("authentication: {}", authentication);

            log.info("인증 성공");
            return authentication;

        } catch (AuthenticationException e) {
            // 인증 실패 시 401 상태 설정 및 JSON 응답 작성
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Failed to login.\"}");
            } catch (IOException ioException) {
                // 에러 발생 시에도 로그를 최소화
                log.warn("Failed to write authentication error response.");
            }
            // 로그 출력 생략
            return null;
        } catch (IOException e) {
            log.warn("Failed to parse authentication request body");
            throw new RuntimeException("Invalid request");
        }

    }

    //인증 성공 메서드
    // 인증 성공시 JWT 생성
    // JWT를 response header에 담기
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("인증 성공");
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        String userId = userDetails.getUser().getId();
        String userRole = userDetails.getUser().getAuthority().getRole();
        String nanoid = userDetails.getUser().getNanoid();

        // JWT 생성
        String jwt = jwtTokenProvider.createToken(userId, userRole, nanoid);
        response.setHeader(JwtContstants.TOKEN_HEADER, JwtContstants.TOKEN_PREFIX + jwt);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write("""
                {
                    "token": "%s"
                    "nanoid": "%s"
                }
                """.formatted(jwt, nanoid));
    }
}
