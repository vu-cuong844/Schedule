package com.example.authentication.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.authentication.dto.UserUpdateProfileRequest;
import com.example.authentication.dto.UserUpdateProfileResponse;
import com.example.authentication.model.User;
import com.example.authentication.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username).orElse(null);
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }

    public UserUpdateProfileResponse updateUser(String id, UserUpdateProfileRequest request) {
        //TODO: viết logic update thông tin user
        return null;
    }
}
