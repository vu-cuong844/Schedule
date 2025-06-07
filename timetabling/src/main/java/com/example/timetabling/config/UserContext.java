package com.example.timetabling.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserContext {
    private String username;
    private String role;
    private String teacherCode;
}