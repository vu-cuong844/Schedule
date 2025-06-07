package com.example.timetabling.dto.algorithm;

import com.example.timetabling.model.Type;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ClassRoomAssignment {
    private String id;
    private String maHP;
    private int thoiLuong;
    private String room;
    private int week;
    private int ngay;
    private int start;
    private int end;
    private Type type;
    private double time_teaching;
    private int slMax;

    public void setTime_teaching(double time_teaching) {
        this.time_teaching = time_teaching;
    }

    public int getSlMax() {
        return slMax;
    }

    public void setSlMax(int slMax) {
        this.slMax = slMax;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaHP() {
        return maHP;
    }

    public void setMaHP(String maHP) {
        this.maHP = maHP;
    }

    public int getThoiLuong() {
        return thoiLuong;
    }

    public void setThoiLuong(int thoiLuong) {
        this.thoiLuong = thoiLuong;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getNgay() {
        return ngay;
    }

    public void setNgay(int ngay) {
        this.ngay = ngay;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getTime_teaching() {
        return time_teaching;
    }

    public void setTime_teaching(int slMax) {
        if (slMax > 100) {
            this.time_teaching = (this.end - this.start + 1) * 1.5;
        } else if (slMax > 40) {
            this.time_teaching = (this.end - this.start + 1) * 1.2;
        } else {
            this.time_teaching = (this.end - this.start + 1) * 1.0;
        }
    }

    public void setTime_teaching1(double time_teaching){
        this.time_teaching = time_teaching;
    }
}
