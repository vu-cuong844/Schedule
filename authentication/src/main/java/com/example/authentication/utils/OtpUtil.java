package com.example.authentication.utils;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import com.example.authentication.model.User;
import com.example.authentication.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class OtpUtil {

    private final UserService userService;

    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALIDITY_MINUTES = 5;
    private static final String OTP_KEY_PREFIX = "otp:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public OtpUtil(UserService userService) {
        this.userService = userService;
    }

    public String generateOTP(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        String otp = generateRandomOtp();
        String key = OTP_KEY_PREFIX + email;

        redisTemplate.opsForValue().set(key, otp, OTP_VALIDITY_MINUTES, TimeUnit.MINUTES);
        return otp;
    }

    private String generateRandomOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }

    public boolean validateOTP(String email, String otp) {
        if (email == null || otp == null) {
            return false;
        }

        // có thể không cần thiết trong thường hợp user quên email và cần email bên thứ
        // 3
        // hoặc tôi sẽ cung cấp phương thức reset mới trong tương lai
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return false;
        }

        String key = OTP_KEY_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp != null && storedOtp.equals(otp)) {
            redisTemplate.delete(key);
            return true;
        }

        return false;
    }
}
