package com.example.timetabling.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.timetabling.config.UserContext;
import com.example.timetabling.config.UserContextHolder;
import com.example.timetabling.dto.response.Response;
import com.example.timetabling.model.TimeTable;
import com.example.timetabling.service.TimeTableService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/schedule/timetable")
public class TimeTableController {
    @Autowired
    private TimeTableService timeTableService;

    @GetMapping("")
    public ResponseEntity<Response<TimeTable>> getTimeTableOf(@RequestParam int term) {

        UserContext userContext = UserContextHolder.getContext();
        if (!hasRole(userContext, "PDT", "TEACHER")) {
            return ResponseEntity.ok(Response.<TimeTable>builder()
                    .data(null)
                    .success(false)
                    .message("You do not have permission to access this resource.")
                    .build());
        }
        Response<TimeTable> response = timeTableService.getTimeTableOf(userContext.getTeacherCode(), term);
        return ResponseEntity.ok(response);
    }

    

    private boolean hasRole(UserContext userContext, String... roles) {
        if (userContext == null || userContext.getRole() == null) {
            return false;
        }

        for (String role : roles) {
            if (userContext.getRole().contains(role)) {
                return true;
            }
        }
        return false;
    }

}
