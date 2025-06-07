package com.example.authentication.model;

import java.util.List;

import com.google.auto.value.AutoValue.Builder;

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
@Builder
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
}
