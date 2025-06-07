package com.example.authentication.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.authentication.model.Token;
import com.example.authentication.model.TokenType;
import com.example.authentication.model.User;
import com.example.authentication.repository.TokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
    @Autowired
    private final TokenRepository tokenRepository;

    //kiểm tra token hợp lệ
    public boolean isValidToken(String token){
        Optional<Token> storedToken = tokenRepository.findByToken(token);
        return storedToken.isPresent() && !storedToken.get().isExpired() && !storedToken.get().isRevoked();
    }

    //Thu hồi token
    public void revokeToken(String token){
        tokenRepository.findByToken(token).ifPresent(t -> {
            t.setRevoked(true);
            t.setExpired(true);
            tokenRepository.save(t);
        });
    }

    //lưu token
    public void saveToken(User user, String tokenString){

        Token token = Token.builder()
                            .token(tokenString)
                            .expired(false)
                            .revoked(false)
                            .user(user)
                            .tokenType(TokenType.REFRESH)
                            .build();
        
        tokenRepository.save(token);
    }

}
