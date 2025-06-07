package com.example.timetabling.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimetableItem {
    @Id
    private String idClass;

    @Column(nullable = false)
    private String idSubject;

    @Column(nullable = false)
    private String nameSubject;

    @Column(nullable = false)
    private String nameEnglishSubject;

    @Column(nullable = false)
    private String weight;

    @Column(nullable = false)
    private int day;

    @Column(nullable = false)
    private int timeStart;

    @Column(nullable = false)
    private int timeENd;

    @Column(nullable = false)
    private int start;

    @Column(nullable = false)
    private int end;

    @Column(nullable = false)
    private String session;


    @ElementCollection
    @CollectionTable(name = "class_week", joinColumns = @JoinColumn(name = "class_week"))
    @Column(nullable = false)
    private List<Integer> weeks;

    @Column(nullable = false)
    private String room;

    @Column(nullable = false)
    private Boolean requetTN;

    @Column(nullable = false)
    private int slMax;

    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    private String managementCode;

    @Column(nullable = false)
    private String teachingType;

    @Column(nullable = false)
    private String teacherName;

    @Column(nullable = false)
    private String teacherCode;

    @Column(nullable = false)
    private int term;

}
