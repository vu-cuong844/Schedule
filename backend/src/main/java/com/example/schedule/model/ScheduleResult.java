package com.example.schedule.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResult {
    private String id;
    private String maHP;
    private int thoiLuong;
    private String room;
    private int week;
    private int ngay;
    private int start;
    private int end;
    private Type type;
}
