package com.example.shoppingmall.controller;

import com.example.shoppingmall.domain.user.User;
import com.example.shoppingmall.security.AuthService;
import com.example.shoppingmall.security.CookieUtil;
import com.example.shoppingmall.security.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final CookieUtil cookieUtil;

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletResponse response) {
        User user = authService.authenticate(email, password);
        List<String> roles = authService.roles(user.getId());
        long rv = authService.bumpRefreshVersion(user.getId());

        String access = jwtProvider.createAccess(user.getId(), user.getEmail(), roles, rv);
        String refresh = jwtProvider.createRefresh(user.getId(), user.getEmail(), roles, rv);

        cookieUtil.addHttpOnlyCookie(response, "ACCESS", access,15 * 60, "Lax", false);
        cookieUtil.addHttpOnlyCookie(response, "REFRESH", refresh,14 * 24 * 3600, "Lax", false);

        return "redirect:/";
    }
}
