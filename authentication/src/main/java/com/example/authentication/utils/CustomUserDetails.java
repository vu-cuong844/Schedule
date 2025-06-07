package com.example.authentication.utils;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
    private String username;
    private String password;
    private String teacherCode;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String username, String password, String teacherCode, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.teacherCode = teacherCode;
        this.authorities = authorities;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Các method mặc định khác (không thay đổi)
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
