package com.example.authentication.controller;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.authentication.dto.MessageMail;
import com.example.authentication.dto.SendEmailResponse;
import com.example.authentication.dto.UserChangePasswordRequest;
import com.example.authentication.dto.UserChangePasswordResponse;
import com.example.authentication.dto.UserLoginRequets;
import com.example.authentication.dto.UserLoginResponse;
import com.example.authentication.dto.UserRegisterRequest;
import com.example.authentication.dto.UserResetPasswordRequest;
import com.example.authentication.dto.UserResetPasswordResponse;
import com.example.authentication.model.User;
import com.example.authentication.service.AuthService;
import com.example.authentication.service.EmailService;
import com.example.authentication.service.TokenService;
import com.example.authentication.service.UserService;
import com.example.authentication.utils.JwtUtil;
import com.example.authentication.utils.OtpUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final AuthService authService;

    private final UserService userService;

    private final TokenService tokenService;

    private final EmailService emailService;

    private final OtpUtil otpUtil;

    @Autowired
    private JwtUtil jwtUtil;

    public UserController(AuthService authService, UserService userService, TokenService tokenService,
            EmailService emailService, OtpUtil otpUtil) {
        this.userService = userService;
        this.authService = authService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.otpUtil = otpUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody List<UserRegisterRequest> requests) {
        try {
            System.out.println("nhận được:" + requests.size());
            List<User> newUser = authService.register(requests);
            System.out.println("Lưu được: " + newUser.size());
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi đăng ký: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequets userLoginRequets, HttpServletResponse response) {
        UserLoginResponse userLoginResponse = authService.login(userLoginRequets);

        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", userLoginResponse.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMinutes(30))
                .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", userLoginResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(userLoginResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || !jwtUtil.validateToken(refreshToken) || !tokenService.isValidToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token không hợp lệ hoặc hết hạn");
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String role = jwtUtil.getRolesFromToken(refreshToken);
        String teacherCode = jwtUtil.getTeacgerCodeFrom(refreshToken);

        String newAccessToken = jwtUtil.generateToken(username, role, teacherCode);
        User user = userService.getUserByUsername(username);

        return ResponseEntity.ok(UserLoginResponse.builder()
                .user(user)
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build());

    }

    @PostMapping("/logout")
    public ResponseEntity<?> postMethodName(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || !jwtUtil.validateToken(refreshToken) || !tokenService.isValidToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token không hợp lệ hoặc hết hạn");
        }

        tokenService.revokeToken(refreshToken);
        SecurityContextHolder.clearContext();

        Cookie deletedCookie = new Cookie("refresh_token", null);
        deletedCookie.setMaxAge(0);
        deletedCookie.setPath("/");
        deletedCookie.setHttpOnly(true);
        deletedCookie.setSecure(true);
        response.addCookie(deletedCookie);

        return ResponseEntity.ok(Map.of("Message", "Logout successfull"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> channgePassword(@RequestBody UserChangePasswordRequest userChangePasswordRequest,
            HttpServletRequest request) {
        UserChangePasswordResponse userChangePasswordResponse = authService.changePassword(userChangePasswordRequest);

        String refreshToken = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken != null) {
            tokenService.revokeToken(refreshToken);
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(userChangePasswordResponse);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody UserResetPasswordRequest request) {
        UserResetPasswordResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgetPassword(@RequestBody String email) {
        String otp = otpUtil.generateOTP(email);
        MessageMail messageMail = MessageMail.builder()
                .from(null)
                .to(new String[] { email })
                .subject("Verify email")
                .content(otp)
                .build();

        SendEmailResponse sendEmailResponse = emailService.sendTextEmail(messageMail);

        return ResponseEntity.ok(sendEmailResponse);
    }

}
