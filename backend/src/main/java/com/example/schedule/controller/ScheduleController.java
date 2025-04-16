package com.example.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.example.schedule.dto.ScheduleRoomRequest;
import com.example.schedule.dto.ScheduleRoomResponse;
import com.example.schedule.service.ScheduleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/schedule-room")
    public ResponseEntity<?> postMethodName(@RequestBody ScheduleRoomRequest request) {
        ScheduleRoomResponse response = scheduleService.scheduleRoom(request);    
        return ResponseEntity.ok(response);
    }
    
}
