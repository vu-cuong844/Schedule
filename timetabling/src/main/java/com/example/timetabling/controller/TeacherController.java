package com.example.timetabling.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.timetabling.config.UserContext;
import com.example.timetabling.model.Teacher;
import com.example.timetabling.service.TeacherService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/schedule/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;
    @GetMapping("/")
    public ResponseEntity<List<Teacher>> getMethodName() {
        List<Teacher> response = teacherService.getAll();
        return ResponseEntity.ok(response);
    }

    private boolean hasPDTRole(UserContext userContext) {
        return userContext != null &&
                userContext.getRole() != null &&
                userContext.getRole().contains("PDT");
    }

}
