package com.example.schedule.dto;

import java.util.List;

import com.example.schedule.model.Class;
import com.example.schedule.model.ScheduleResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRoomResponse {
    private List<ScheduleResult> classes;
    private List<Class> unassignableClasses;
    private boolean isScheduled;
    private String message;
}
