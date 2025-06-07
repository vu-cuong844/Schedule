package com.example.timetabling.dto.request;

import java.util.List;

import com.example.timetabling.model.Class;
import com.example.timetabling.model.Room;
import com.example.timetabling.model.Teacher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleRequest {
    private List<Class> classes;
    private List<Teacher> teachers;
    private List<Room> rooms;
    private int term;
}
