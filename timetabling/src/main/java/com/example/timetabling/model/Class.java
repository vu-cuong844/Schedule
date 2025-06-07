package com.example.timetabling.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Class {
    private String id;
    private String maHP;
    private int slMax;
    private int thoiLuong;
    private Type type;
}
