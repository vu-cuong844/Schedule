package com.example.authentication.dto;

import com.example.authentication.model.AuthProvider;

import lombok.Data;

@Data
public class UserLoginRequets {
    private String username;
    private String password;

    //phần dưới phục vụ cho login bằng bên thứ 3, chưa cần quan tâm
    private AuthProvider provider;
    private String providerToken;
}
