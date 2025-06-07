package com.example.authentication.dto;

import com.example.authentication.model.AuthProvider;
import com.example.authentication.model.Role;
import com.example.authentication.model.Teacher;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    private Teacher teacher;

    //nếu đang ký qua bên thứ 3
    private AuthProvider provider;
    private String providerId;
    private String providerToken;

    private Role role;
}
