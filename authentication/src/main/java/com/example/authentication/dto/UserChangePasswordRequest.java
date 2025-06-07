package com.example.authentication.dto;

import com.google.auto.value.AutoValue.Builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
