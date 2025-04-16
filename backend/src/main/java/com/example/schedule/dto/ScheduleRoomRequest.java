package com.example.schedule.dto;

import java.util.List;

import com.example.schedule.model.Class;
import com.example.schedule.model.Room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRoomRequest {
    private List<Room> rooms;
    private List<Class> classes;

}
