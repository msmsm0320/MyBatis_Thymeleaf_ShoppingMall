package com.example.shoppingmall.controller;

import com.example.shoppingmall.domain.user.User;
import com.example.shoppingmall.security.AuthService;
import com.example.shoppingmall.security.CookieUtil;
import com.example.shoppingmall.security.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final CookieUtil cookieUtil;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletResponse response) {
        User user = authService.authenticate(email, password);
        log.info("user_email = ${}", user.getEmail());
        List<String> roles = authService.roles(user.getId());
        long rv = authService.bumpRefreshVersion(user.getId());

        String access = jwtProvider.createAccess(user.getId(), user.getEmail(), roles, rv);
        String refresh = jwtProvider.createRefresh(user.getId(), user.getEmail(), roles, rv);

        cookieUtil.addHttpOnlyCookie(response, "ACCESS", access,15 * 60, "Lax", false);
        cookieUtil.addHttpOnlyCookie(response, "REFRESH", refresh,14 * 24 * 3600, "Lax", false);

        return "redirect:/";
    }

    @GetMapping("/signup")
    public String signup(){
        return "signup";
    }

    @PostMapping("/signup")
    public String singup(@RequestParam String email,
                         @RequestParam String password,
                         @RequestParam String nickname){

        authService.signup(email, password, nickname);
        return "redirect:/login";
    }
}


