package com.example.timetabling.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.timetabling.config.UserContext;
import com.example.timetabling.config.UserContextHolder;
import com.example.timetabling.dto.request.CreateSubjectRequest;
import com.example.timetabling.dto.request.SearcheSubjectRequest;
import com.example.timetabling.dto.response.Response;
import com.example.timetabling.model.Subject;
import com.example.timetabling.service.SubjectSevice;

import java.util.List;

@RestController
@RequestMapping("/api/schedule/subject")
public class SubjectController {
    @Autowired
    private SubjectSevice subjectSevice;

    @PostMapping("/")
    public ResponseEntity<Response<List<Subject>>> addSubjects(@RequestBody CreateSubjectRequest request) {
        UserContext userContext = UserContextHolder.getContext();
        if (!hasAdminRole(userContext)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.<List<Subject>>builder()
                    .success(false)
                    .message("Không có quyền thực hiện thao tác này")
                    .build());
        }
        
        Response<List<Subject>> response = subjectSevice.createSubbjects(request.getSubjects());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/")
    public ResponseEntity<Response<Subject>> updateSubject(@RequestBody Subject subject) {
        UserContext userContext = UserContextHolder.getContext();
        if (!hasAdminRole(userContext)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.<Subject>builder()
                    .success(false)
                    .message("Không có quyền thực hiện thao tác này")
                    .build());
        }

        try {
            Response<Subject> response = subjectSevice.updateSubject(subject);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(Response.<Subject>builder()
                    .success(false)
                    .message("Lỗi cập nhật môn học: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Response<List<Subject>>> searchSubjects(@RequestBody SearcheSubjectRequest request) {
        Response<List<Subject>> response = subjectSevice.searchSubject(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<Response<List<Subject>>> getAllSubject() {
        Response<List<Subject>> response = subjectSevice.getAllSubjects();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> deleteSubject(@PathVariable String id) {
        UserContext userContext = UserContextHolder.getContext();
        if (!hasAdminRole(userContext)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.<String>builder()
                    .success(false)
                    .message("Không có quyền thực hiện thao tác này")
                    .build());
        }

        Response<String> response = subjectSevice.deleteSubject(id);
        return ResponseEntity.ok(response);
    }

    private boolean hasAdminRole(UserContext userContext) {
        return userContext != null && 
               userContext.getRole() != null && 
               userContext.getRole().contains("PDT");
    }
}