package com.cast.caspedia.security.jwt.provider;

import com.cast.caspedia.security.custom.CustomUserDetails;
import com.cast.caspedia.security.jwt.constants.JwtContstants;
import com.cast.caspedia.security.jwt.prop.JwtProp;
import com.cast.caspedia.user.domain.Authority;
import com.cast.caspedia.user.domain.User;
import com.cast.caspedia.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/*
 * JWT 토큰 관련 기능을 제공하는 클래스
 * - 토큰 생성
 * - 토큰 해석
 * - 토큰 유효성 검사
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private JwtProp jwtProp;

    private UserRepository userRepository;

    public JwtTokenProvider(JwtProp jwtProp, UserRepository userRepository) {
        this.jwtProp = jwtProp;
        this.userRepository = userRepository;
    }

    //JWT 토큰 생성
    public String createToken(String id, String role, String nanoid) {

        String jwt = Jwts.builder()
                .signWith(getShaKey(), Jwts.SIG.HS512)
                .header()                                                      // update (version : after 1.0)
                .add("typ", JwtContstants.TOKEN_TYPE)              // 헤더 설정
                .and()
                .expiration(new Date(System.currentTimeMillis() + 864000000))  // 토큰 만료 시간 설정 (10일)
                .claim("uid", id)                                     // 클레임 설정: 사용자 아이디
                .claim("rol", role)                                      // 클레임 설정: 권한
                .claim("nid", nanoid)
                .compact();
        return jwt;
    }

    /*
     * 🔐➡👩‍💼 토큰 해석
     *
     * Authorization : Bearer + {jwt}  (authHeader)
     * ➡ jwt 추출
     * ➡ UsernamePasswordAuthenticationToken
     */
    public UsernamePasswordAuthenticationToken getAuthentication(String authHeader) {

        log.info("authHeader : " + authHeader);

        if(authHeader == null || authHeader.length() == 0 )
            return null;

        try {
            // jwt 추출
            String jwt = authHeader.replace(JwtContstants.TOKEN_PREFIX, "");

            // 🔐➡👩‍💼 JWT 파싱
            Jws<Claims> parsedToken = Jwts.parser()
                    .verifyWith(getShaKey())
                    .build()
                    .parseSignedClaims(jwt);

            log.info("parsedToken : " + parsedToken);

            // 인증된 사용자 아이디
            String userId = parsedToken.getPayload().get("uid").toString();
            log.info("userId : " + userId);

            // 인증된 사용자 권한
            Claims claims = parsedToken.getPayload();
            String role = (String) claims.get("rol");
            log.info("roles : " + role);


            // 토큰에 userId 있는지 확인
            if( userId == null || userId.length() == 0 )
                return null;


            User user = new User();
            user.setId(userId);
            // OK: 권한도 바로 Users 객체에 담아보기
            Authority authority = new Authority();
            authority.setRole(role);
            user.setAuthority(authority);

            // OK
            // CustomeUser 에 권한 담기
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

            // 토큰 유효하면
            // name, enabled 추가 정보 조회
            try {
                User userInfo = userRepository.findUserById(userId);
                if( userInfo != null ) {
                    user.setName(userInfo.getName());
                    user.setEnabled(userInfo.isEnabled());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error("토큰 유효 -> DB 추가 정보 조회시 에러 발생...");
            }

            // 사용자가 삭제된경우 null 반환
            if(!user.isEnabled()) {
                return null;
            }

            CustomUserDetails userDetails = new CustomUserDetails(user);

            // OK
            // new UsernamePasswordAuthenticationToken( 사용자정보객체, 비밀번호, 사용자의 권한(목록)  );
            return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        } catch (ExpiredJwtException exception) {
            log.warn("Request to parse expired JWT : {} failed : {}", authHeader, exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.warn("Request to parse unsupported JWT : {} failed : {}", authHeader, exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.warn("Request to parse invalid JWT : {} failed : {}", authHeader, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.warn("Request to parse empty or null JWT : {} failed : {}", authHeader, exception.getMessage());
        }
        return null;
    }



    //
    /**
     * 🔐❓ 토큰 유효성 검사
     * @param jwt
     * @return
     *  ⭕ true     : 유효
     *  ❌ false    : 만료
     */
    public boolean validateToken(String jwt) {

        try {
            // 🔐➡👩‍💼 JWT 파싱
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(getShaKey())
                    .build()
                    .parseSignedClaims(jwt);

            log.info("::::: 토큰 만료기간 :::::");
            log.info("-> " + claims.getPayload().getExpiration());
            /*
                PAYLOAD
                {
                    "exp": 1703140095,        ⬅ 만료기한 추출
                    "uid": "joeun",
                    "rol": [
                        "ROLE_USER"
                    ]
                }
            */
            return !claims.getPayload().getExpiration().before(new Date());
        } catch (ExpiredJwtException exception) {
            log.error("Token Expired");                 // 토큰 만료
            return false;
        } catch (JwtException exception) {
            log.error("Token Tampered");                // 토큰 손상
            return false;
        } catch (NullPointerException exception) {
            log.error("Token is null");                 // 토큰 없음
            return false;
        } catch (Exception e) {
            return false;
        }
    }


    private byte[] getSigningKey() {
        return jwtProp.getSecretKey().getBytes();
    }

    private SecretKey getShaKey() {
        return Keys.hmacShaKeyFor(getSigningKey());
    }

}
