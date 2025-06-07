package com.example.authentication.dto;

import com.example.authentication.model.User;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserLoginResponse {
    private User user;
    private String accessToken;
    private String refreshToken;

    private boolean isLogin;
    private String message;
}
