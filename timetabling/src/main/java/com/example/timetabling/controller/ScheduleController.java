package com.example.timetabling.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.timetabling.config.UserContext;
import com.example.timetabling.config.UserContextHolder;
import com.example.timetabling.dto.request.ScheduleRequest;
import com.example.timetabling.dto.response.Response;
import com.example.timetabling.model.TimeTable;
import com.example.timetabling.service.ScheduleService;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/")
    public ResponseEntity<Response<TimeTable>> schedule(@RequestBody ScheduleRequest request) {
        UserContext userContext = UserContextHolder.getContext();
        if (!hasAdminRole(userContext)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.<TimeTable>builder()
                    .success(false)
                    .message("Không có quyền thực hiện thao tác này")
                    .build());
        }

        System.out.println("Start................. \n \n");

        Response<TimeTable> response = scheduleService.schedule(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Response<TimeTable>> scheduleConfirm(@RequestBody TimeTable timeTable) {
        UserContext userContext = UserContextHolder.getContext();
        if (!hasAdminRole(userContext)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.<TimeTable>builder()
                    .success(false)
                    .message("Không có quyền thực hiện thao tác này")
                    .build());
        }

        Response<TimeTable> response = scheduleService.confirm(timeTable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Schedule service working!");
    }

    private boolean hasAdminRole(UserContext userContext) {
        return userContext != null && 
               userContext.getRole() != null && 
               userContext.getRole().contains("PDT");
    }
}