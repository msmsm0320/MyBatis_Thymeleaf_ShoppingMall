package com.example.shoppingmall.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @Email @NotBlank
    private String email;

    @NotBlank @Size(min = 4, max = 64)
    private String password;

    @NotBlank @Size(max = 50)
    private String nickname;
}
