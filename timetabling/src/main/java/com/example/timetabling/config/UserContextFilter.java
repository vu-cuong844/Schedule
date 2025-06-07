package com.example.timetabling.config;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserContextFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        UserContext userContext = UserContext.builder()
            .username(httpRequest.getHeader("X-User-Username"))
            .role(httpRequest.getHeader("X-User-Role"))
            .teacherCode(httpRequest.getHeader("X-Teacher-Code"))
            .build();
        
        UserContextHolder.setContext(userContext);
        
        try {
            chain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
        }
    }
}