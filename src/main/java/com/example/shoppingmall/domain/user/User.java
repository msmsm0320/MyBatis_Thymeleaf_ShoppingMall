package com.example.shoppingmall.domain.user;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private boolean enabled;
}
