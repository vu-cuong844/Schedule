package com.example.timetabling.dto.algorithm;

import java.util.List;

import com.example.timetabling.model.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassTeacherAssignmentInput {
    private String id;
    private String maHP;
    private int thoiLuong;
    private String room;
    private List<Integer> weeks;
    private int ngay;
    private int start;
    private int end;
    private Type type;
    private double time_teaching;
    private int slMax;
}
