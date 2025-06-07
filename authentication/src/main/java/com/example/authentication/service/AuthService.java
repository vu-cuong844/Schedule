package com.example.authentication.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.authentication.dto.UserChangePasswordRequest;
import com.example.authentication.dto.UserChangePasswordResponse;
import com.example.authentication.dto.UserLoginRequets;
import com.example.authentication.dto.UserLoginResponse;
import com.example.authentication.dto.UserRegisterRequest;
import com.example.authentication.dto.UserResetPasswordRequest;
import com.example.authentication.dto.UserResetPasswordResponse;
import com.example.authentication.model.AuthProvider;
import com.example.authentication.model.Role;
import com.example.authentication.model.Teacher;
import com.example.authentication.model.User;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.utils.CustomUserDetails;
import com.example.authentication.utils.JwtUtil;
import com.example.authentication.utils.OAuth2TokenVerifier;
import com.example.authentication.utils.OtpUtil;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OAuth2TokenVerifier oAuth2TokenVerifier;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private OtpUtil otpUtil;

    public List<User> register(List<UserRegisterRequest> userRegisterRequest) {
        try {
            System.out.println("Nhận lưu: " + userRegisterRequest.size());
            List<User> users = this.processedRequest(userRegisterRequest);
            System.out.println("đã lưu" + users.size() + "giáo viên \n \n");
            return userRepository.saveAll(users);
        } catch (Exception e) {
            System.out.println("Lỗi khi lưu" + e.getMessage() + " kiểm ra lại thôi \n \n");
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public UserLoginResponse login(UserLoginRequets requets) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(requets.getUsername(), requets.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String accessToken = jwtUtil.generateToken(userDetails.getUsername(),
                    userDetails.getAuthorities().toString(), userDetails.getTeacherCode());
            String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername(),
                    userDetails.getAuthorities().toString(), userDetails.getTeacherCode());

            Optional<User> userOptional = userRepository.findByUsername(requets.getUsername());
            if (userOptional.isEmpty()) {
                throw new RuntimeException("User not found");
            }

            User user = userOptional.get();
            tokenService.saveToken(user, refreshToken);

            UserLoginResponse userLoginResponse = UserLoginResponse.builder()
                    .user(userOptional.get())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .isLogin(true)
                    .message("Login successfull!")
                    .build();

            return userLoginResponse;

        } catch (BadCredentialsException e) {
            return UserLoginResponse.builder()
                    .isLogin(false)
                    .message("Invalid username or password")
                    .build();
        } catch (DisabledException e) {
            return UserLoginResponse.builder()
                    .isLogin(false)
                    .message("Account is disabled")
                    .build();
        } catch (LockedException e) {
            return UserLoginResponse.builder()
                    .isLogin(false)
                    .message("Account is locked")
                    .build();
        } catch (Exception e) {
            return UserLoginResponse.builder()
                    .isLogin(false)
                    .message("Login falied: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public UserChangePasswordResponse changePassword(UserChangePasswordRequest userChangePasswordRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();
        System.out.println(username);

        String oldPassword = userChangePasswordRequest.getOldPassword();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return UserChangePasswordResponse.builder()
                    .isSuccess(false)
                    .message("User not found or You not login!")
                    .build();
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return UserChangePasswordResponse.builder()
                    .isSuccess(false)
                    .message("Old password is invalid!")
                    .build();
        }

        String newPassword = userChangePasswordRequest.getNewPassword();
        String confirmPassword = userChangePasswordRequest.getConfirmPassword();

        if (!newPassword.equals(confirmPassword)) {
            return UserChangePasswordResponse.builder()
                    .isSuccess(false)
                    .message("Fail")
                    .build();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());

        try {
            userRepository.save(user);
        } catch (Exception e) {
            return UserChangePasswordResponse.builder()
                    .isSuccess(false)
                    .message(e.getMessage())
                    .build();
        }

        return UserChangePasswordResponse.builder()
                .isSuccess(true)
                .message("Password changed successfully!")
                .build();
    }

    public UserResetPasswordResponse resetPassword(UserResetPasswordRequest request) {
        String otp = request.getOTP();
        String email = request.getEmail();

        if (otp == null || email == null) {
            return UserResetPasswordResponse.builder()
                    .isReseted(false)
                    .message("Email or OTP is empty!")
                    .build();
        }

        if (otpUtil.validateOTP(email, otp)) {
            User user = userRepository.findByEmail(email).orElse(null);
            String newPassword = request.getNewPassword();
            String confirmPassword = request.getConfirmPassword();

            if (newPassword.equals(confirmPassword)) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setPasswordChangedAt(LocalDateTime.now());
                try {
                    userRepository.save(user);

                    return UserResetPasswordResponse.builder()
                            .isReseted(true)
                            .message("Reset password successfull!")
                            .build();
                } catch (Exception e) {
                    return UserResetPasswordResponse.builder()
                            .isReseted(false)
                            .message("Fail: " + e.getMessage())
                            .build();
                }
            } else {
                return UserResetPasswordResponse.builder()
                        .isReseted(false)
                        .message("New password and confirm password are not match!")
                        .build();
            }
        } else {
            return UserResetPasswordResponse.builder()
                    .isReseted(false)
                    .message("OTP is invalid!")
                    .build();
        }

    }

    private List<User> processedRequest(List<UserRegisterRequest> requests) {

        System.out.println("Thực hiện tiền xử lý: " + requests.size());
        List<User> newUsers = new ArrayList<>();
        for (UserRegisterRequest request : requests) {
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent()) {
                newUsers.add(existingUser.get());
                System.out.println("User đã tồn tại \n \n");
                continue;
            }

            User newUser = null;

            if (request.getUsername() != null && !request.getPassword().isEmpty()) {
                // kiểm tra xác nhận password
                if (request.getPassword().equals(request.getConfirmPassword())) {

                    System.out.println("Thực hiện thêm " + request.getTeacher().getName());
                    Teacher teacher = teacherService.addNewTeacher(request.getTeacher());
                    

                    if (teacher == null) {
                        // throw new RuntimeException("New teacher not saved \n \n \n");
                        System.out.println("Không thêm được" + request.getTeacher().getName());
                        continue;
                    }

                    System.out.println("Thực hiện thêm thành công " + teacher.getName());
                    newUser = User.builder()
                            .username(request.getUsername())
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .passwordChangedAt(LocalDateTime.now())
                            .enabled(true)
                            .authProvider(AuthProvider.LOCAL)
                            .role(request.getRole() != null ? request.getRole() : Role.TEACHER)
                            .teacherCode(teacher.getTeacherCode())
                            .build();
                } else {
                    throw new RuntimeException("Password and Confirm Password are not match");
                }
            } else if (request.getProvider() != AuthProvider.LOCAL
                    && request.getProviderToken() != null) {

                if (!oAuth2TokenVerifier.verifyThirdPartyToken(request.getProvider(),
                        request.getProviderToken())) {
                    throw new RuntimeException("Token of " + " " + request.getProvider() + " not valid");
                }

                newUser = User.builder()
                        .username(request.getUsername() != null ? request.getUsername()
                                : request.getEmail())
                        .email(request.getEmail())
                        .password("N/A")
                        .passwordChangedAt(LocalDateTime.now())
                        .enabled(true)
                        .authProvider(request.getProvider())
                        .role(request.getRole() != null ? request.getRole() : Role.TEACHER)
                        .build();
            } else {
                throw new IllegalArgumentException("Request not valid");
            }
            newUsers.add(newUser);
        }

        return newUsers;
    }
}
