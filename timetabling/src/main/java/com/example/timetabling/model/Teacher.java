package com.example.timetabling.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teachers")
public class Teacher {
    @Id
    private String teacherCode;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    @CollectionTable(name = "teacher_subject", joinColumns = @JoinColumn(name = "teacher_code"))
    @Column(nullable = false)
    private List<String> subjectCodes;

    @ElementCollection(targetClass = Type.class)
    @CollectionTable(name = "teacher_type", joinColumns = @JoinColumn(name = "teacher_code"))
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private List<Type> type;

    @Column(nullable = false)
    private String hoc_vi;

    @Column(nullable = false)
    private int time;

    @Column(nullable = false)
    private int priority_gd;

    @Column(nullable = false)
    private int priority_tn;

    @Column(nullable = false)
    private String institute;

    @Column(nullable = false)
    private String department;

    public Teacher(String name, List<String> subjectCodes, List<Type> type, String hoc_vi, int time) {
        this.name = name;
        this.subjectCodes = subjectCodes;
        this.type = type;
        this.hoc_vi = hoc_vi;
        this.time = time;

        // Ưu tiên cho giai đoạn GD: TS > PGS > ThS > khác
        this.priority_gd = hoc_vi.equals("TS") ? 3 : hoc_vi.equals("PGS") ? 2 : hoc_vi.equals("ThS") ? 1 : 0;
        // Ưu tiên cho giai đoạn TN: ThS > GVN > TS > khác
        this.priority_tn = hoc_vi.equals("GVN") ? 3 : hoc_vi.equals("ThS") ? 2 : hoc_vi.equals("TS") ? 1 : 0;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSubjectCodes() {
        return subjectCodes;
    }

    public void setSubjectCodes(List<String> subjectCodes) {
        this.subjectCodes = subjectCodes;
    }

    public List<Type> getType() {
        return type;
    }

    public void setType(List<Type> type) {
        this.type = type;
    }

    public String getHoc_vi() {
        return hoc_vi;
    }

    public void setHoc_vi(String hoc_vi) {
        this.hoc_vi = hoc_vi;
        // Ưu tiên cho giai đoạn GD: TS > PGS > ThS > khác
        this.priority_gd = hoc_vi.equals("TS") ? 3 : hoc_vi.equals("PGS") ? 2 : hoc_vi.equals("ThS") ? 1 : 0;
        // Ưu tiên cho giai đoạn TN: ThS > GVN > TS > khác
        this.priority_tn = hoc_vi.equals("GVN") ? 3 : hoc_vi.equals("ThS") ? 2 : hoc_vi.equals("TS") ? 1 : 0;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getPriority_gd() {
        return priority_gd;
    }

    public void setPriority_gd(int priority_gd) {
        this.priority_gd = priority_gd;
    }

    public int getPriority_tn() {
        return priority_tn;
    }

    public void setPriority_tn(int priority_tn) {
        this.priority_tn = priority_tn;
    }
}
