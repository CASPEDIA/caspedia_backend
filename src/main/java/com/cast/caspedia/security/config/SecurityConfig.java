package com.cast.caspedia.security.config;

import com.cast.caspedia.achievement.service.AchievementService;
import com.cast.caspedia.security.custom.CustomUserDetailService;
import com.cast.caspedia.security.jwt.filter.JwtAuthenticationFilter;
import com.cast.caspedia.security.jwt.filter.JwtRequestFilter;
import com.cast.caspedia.security.jwt.provider.JwtTokenProvider;
import com.cast.caspedia.security.prop.FrontServerProp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
//@preAuthorize, @postAuthorize, @secured 어노테이션을 사용하기 위해 활성화
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FrontServerProp frontServerProp;

    @Autowired
    private AchievementService achievementService;

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        // 폼 기반 로그인 비활성화
        http.formLogin(login ->login.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource())
        );

        // HTTP 기본 인증 비활성화
        http.httpBasic(basic ->basic.disable());

        // CSRF(Cross-Site Request Forgery) 공격 방어 기능 비활성화
        http.csrf(csrf ->csrf.disable());

        // 세션 관리 정책 설정: STATELESS로 설정하면 서버는 세션을 생성하지 않음
        // 🔐 세션을 사용하여 인증하지 않고,  JWT 를 사용하여 인증하기 때문에, 세션 불필요
        http.sessionManagement(management ->management
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // ✅ JWT 요청 필터 1️⃣
        // ✅ JWT 인증 필터 2️⃣
        http.addFilterAt(new JwtAuthenticationFilter(authenticationManager, jwtTokenProvider, objectMapper, achievementService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtRequestFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
        ;

        http.authorizeHttpRequests(authorize ->authorize
                // 정적 자원 요청은 인증 없이 허용
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                // 로그인, 로그아웃 요청은 인증 없이 허용
                .requestMatchers("/api/user/login").permitAll()
                .requestMatchers("/api/user/join").permitAll()

//                // test : 관리자 회원가입 요청은 인증 없이 허용
//                .requestMatchers("/api/admin/join").permitAll()
//                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated())
        ;

        // 사용자 정보를 불러오는 서비스 설정
        http.userDetailsService(customUserDetailService);

        // 구성이 완료된 SecurityFilterChain을 반환합니다.
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true); // 인증 정보(쿠키 등)를 허용하려면 true 설정
        configuration.setAllowedOrigins(frontServerProp.getUrlList()); // 프론트엔드 주소
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
