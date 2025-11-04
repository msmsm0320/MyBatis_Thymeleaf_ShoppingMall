package com.example.shoppingmall.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public void addHttpOnlyCookie(HttpServletResponse response, String name, String value, int maxAgeSec, String sameSite, boolean secure){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/"); // 모든 경로에서 유효하게 설정
        cookie.setMaxAge(maxAgeSec); // 유효시간 (초)
        cookie.setHttpOnly(true); // JS 접근 차단
        cookie.setSecure(secure); // HTTPS 요청인지 확인
        response.addHeader("Set-Cookie", String.format(
                "%s=%s; Path=/, Max-aged=%d; HttpOnly; SameSite=%s%s",
                cookie.getName(),
                cookie.getValue(),
                cookie.getMaxAge(),
                sameSite,
                secure ? "; Secure" : ""
        ));
    }

    public void expire(HttpServletResponse response, String name){
        response.addHeader("Set-Cookie", name + "=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax");
    }
}
