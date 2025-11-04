package com.example.shoppingmall.security;

import com.example.shoppingmall.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailService userDetailService;
    private final UserMapper userMapper;

    private static final List<String> WHITE_LIST = List.of(
            "/login", "/signup", "/css", "/js", "/image/", "/uploads/"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if(SecurityContextHolder.getContext().getAuthentication() == null){
            String accessToken = readCookie(request, "ACCESS");

            if(accessToken != null && !accessToken.isEmpty()){
                try{
                    // 1) 파싱 + 서명/만료 검증
                    var jws = jwtProvider.parse(accessToken);
                    Claims claims = jws.getBody();

                    // 2) rv 동기화(서버 저장값 == 토큰값 ?)
                    Long userId = Long.valueOf(claims.getSubject());
                    Long tokenRv = claims.get("tokenRv", Long.class);
                    Long serverRv = userMapper.findRefreshVersion(userId);
                    Long serverRvVal = (serverRv == null ? 0L : serverRv);

                    if (tokenRv != null && tokenRv == serverRvVal) {
                        // 3) 사용자 로드 + 인증 주입
                        String email = claims.get("email", String.class);
                        UserDetails user = userDetailService.loadUserByUsername(email);

                        var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                    // rv 불일치면 인증 주입 안 함(익명으로 진행)

                } catch (ExpiredJwtException exception) {

                } catch (JwtException exception){

                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
