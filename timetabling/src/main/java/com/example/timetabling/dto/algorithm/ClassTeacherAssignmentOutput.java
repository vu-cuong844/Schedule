package com.example.timetabling.dto.algorithm;

import com.example.timetabling.model.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassTeacherAssignmentOutput {
    private String id;
    private String maHP;
    private Type type;
    private int week;
    private int day;
    private int start;
    private int end;
    private String room;
    private double time_teaching;
    private String teacherCode;
    private String hocvi;
    private int slMax;
}
