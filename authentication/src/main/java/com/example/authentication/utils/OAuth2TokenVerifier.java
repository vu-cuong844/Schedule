package com.example.authentication.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import com.example.authentication.model.AuthProvider;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.json.webtoken.JsonWebToken.Payload;
import com.google.auth.oauth2.TokenVerifier;

@Service
public class OAuth2TokenVerifier {
    
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    public boolean verifyThirdPartyToken(AuthProvider provider, String providerToken) {
        switch (provider) {
            case GOOGLE:
                return verifyGoogleToken(providerToken);
            case FACEBOOK:
                return verifyFacebookToken(providerToken);
            case GITHUB:
                return verifyGithubToken(providerToken);
            default:
                return false;
        }
    }

    private boolean verifyGoogleToken(String token) {
        try {
            TokenVerifier verifier = TokenVerifier.newBuilder().setAudience(googleClientId).build();
            JsonWebSignature jws = verifier.verify(token);

            if (jws != null){
                Payload payload = jws.getPayload();
                return true;
            }
        
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private boolean verifyFacebookToken(String token) {
        // Viết logic xác thực token Facebook
        return false;
    }

    private boolean verifyGithubToken(String token) {
        // Viết logic xác thực token GitHub
        return false;
    }
}
