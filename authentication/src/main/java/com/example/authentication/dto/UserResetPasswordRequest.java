package com.example.authentication.dto;

import com.google.auto.value.AutoValue.Builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResetPasswordRequest {
    private String email;
    private String OTP;
    private String newPassword;
    private String confirmPassword;
}
